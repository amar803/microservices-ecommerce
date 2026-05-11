package com.ecommerce.orderservice.service;

import com.ecommerce.common.dto.OrderDto;
import com.ecommerce.common.dto.OrderItemDto;
import com.ecommerce.common.dto.OrderStatus;
import com.ecommerce.common.dto.PaymentDto;
import com.ecommerce.common.event.EventEnvelope;
import com.ecommerce.common.exception.NotFoundException;
import com.ecommerce.common.messaging.KafkaTopics;
import com.ecommerce.orderservice.domain.OrderEntity;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.web.CreateOrderItemRequest;
import com.ecommerce.orderservice.web.CreateOrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, EventEnvelope<OrderDto>> kafkaTemplate;
    private final OrderWorkflowClient orderWorkflowClient;

    public OrderService(
            OrderRepository orderRepository,
            ObjectMapper objectMapper,
            KafkaTemplate<String, EventEnvelope<OrderDto>> kafkaTemplate,
            OrderWorkflowClient orderWorkflowClient
    ) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.orderWorkflowClient = orderWorkflowClient;
    }

    @Transactional
    public OrderDto create(CreateOrderRequest request) {
        List<OrderItemDto> items = request.items().stream()
                .map(this::toOrderItem)
                .toList();

        BigDecimal total = items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity entity = new OrderEntity();
        entity.setUserId(request.userId());
        entity.setTotalAmount(total);
        entity.setStatus(OrderStatus.CREATED);
        entity.setItemsJson(serialize(items));

        OrderEntity saved = orderRepository.save(entity);

        try {
            reserveInventory(saved, items);
            saved.setStatus(OrderStatus.PAYMENT_PENDING);
            orderRepository.save(saved);

            PaymentDto authorized = orderWorkflowClient.authorizePayment(
                    saved.getId(),
                    saved.getTotalAmount(),
                    "ord-" + saved.getId() + "-" + UUID.randomUUID()
            );
            orderWorkflowClient.capturePayment(authorized.id());

            saved.setStatus(OrderStatus.PAID);
            OrderEntity paidOrder = orderRepository.save(saved);

            OrderDto order = toDto(paidOrder);
            publishOrderEvent("ORDER_PAID", paidOrder.getId(), order);

            orderWorkflowClient.sendOrderNotification(order.userId(), order.id(), order.totalAmount());
            orderWorkflowClient.publishReportEvent("ORDER_COMPLETED");
            return order;
        } catch (Exception ex) {
            releaseInventorySafely(items);
            saved.setStatus(OrderStatus.FAILED);
            orderRepository.save(saved);
            orderWorkflowClient.publishReportEvent("ORDER_FAILED");

            if (ex instanceof OrchestrationException orchestrationException) {
                throw orchestrationException;
            }
            throw new OrchestrationException("Order orchestration failed: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public OrderDto getById(Long orderId) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        return toDto(entity);
    }

    private OrderItemDto toOrderItem(CreateOrderItemRequest item) {
        return new OrderItemDto(item.productId(), item.sku().trim().toUpperCase(), item.quantity(), item.unitPrice());
    }

    private void reserveInventory(OrderEntity order, List<OrderItemDto> items) {
        for (OrderItemDto item : items) {
            boolean reserved = orderWorkflowClient.reserveInventory(item.productId(), item.sku(), item.quantity());
            if (!reserved) {
                throw new OrchestrationException("Insufficient inventory for sku " + item.sku());
            }
        }
        order.setStatus(OrderStatus.RESERVED);
        orderRepository.save(order);
    }

    private void releaseInventorySafely(List<OrderItemDto> items) {
        for (OrderItemDto item : items) {
            try {
                orderWorkflowClient.releaseInventory(item.productId(), item.sku(), item.quantity());
            } catch (Exception ignored) {
                // Best-effort compensation to avoid masking original failure.
            }
        }
    }

    private void publishOrderEvent(String eventType, Long orderId, OrderDto order) {
        EventEnvelope<OrderDto> envelope = EventEnvelope.of(
                eventType,
                "order-service",
                String.valueOf(orderId),
                order
        );
        kafkaTemplate.send(KafkaTopics.ORDERS_EVENTS, String.valueOf(orderId), envelope);
    }

    private OrderDto toDto(OrderEntity entity) {
        return new OrderDto(
                entity.getId(),
                entity.getUserId(),
                deserialize(entity.getItemsJson()),
                entity.getTotalAmount(),
                entity.getStatus()
        );
    }

    private String serialize(List<OrderItemDto> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize order items", e);
        }
    }

    private List<OrderItemDto> deserialize(String raw) {
        try {
            return objectMapper.readValue(raw, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize order items", e);
        }
    }
}
