package com.ecommerce.orderservice.service;

import com.ecommerce.common.dto.OrderDto;
import com.ecommerce.common.dto.OrderStatus;
import com.ecommerce.common.dto.PaymentDto;
import com.ecommerce.common.dto.PaymentStatus;
import com.ecommerce.common.event.EventEnvelope;
import com.ecommerce.common.exception.NotFoundException;
import com.ecommerce.common.messaging.KafkaTopics;
import com.ecommerce.orderservice.domain.OrderEntity;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.web.CreateOrderItemRequest;
import com.ecommerce.orderservice.web.CreateOrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, EventEnvelope<OrderDto>> kafkaTemplate;

    @Mock
    private OrderWorkflowClient orderWorkflowClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, new ObjectMapper(), kafkaTemplate, orderWorkflowClient);
    }

    @Test
    void createShouldPersistOrderAndPublishEvent() {
        CreateOrderRequest request = new CreateOrderRequest(
                5L,
                List.of(new CreateOrderItemRequest(11L, "sku-1", 2, new BigDecimal("15.50")))
        );

        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity entity = invocation.getArgument(0);
            ReflectionTestUtils.setField(entity, "id", 99L);
            return entity;
        });

        when(orderWorkflowClient.reserveInventory(11L, "SKU-1", 2)).thenReturn(true);
        when(orderWorkflowClient.authorizePayment(any(), any(), any())).thenReturn(
                new PaymentDto(501L, 99L, new BigDecimal("31.00"), "USD", PaymentStatus.AUTHORIZED, "idem-1")
        );
        when(orderWorkflowClient.capturePayment(501L)).thenReturn(
                new PaymentDto(501L, 99L, new BigDecimal("31.00"), "USD", PaymentStatus.CAPTURED, "idem-1")
        );

        OrderDto dto = orderService.create(request);

        assertEquals(99L, dto.id());
        assertEquals(new BigDecimal("31.00"), dto.totalAmount());
        assertEquals(OrderStatus.PAID, dto.status());
        verify(kafkaTemplate).send(eq(KafkaTopics.ORDERS_EVENTS), eq("99"), any(EventEnvelope.class));
    }

    @Test
    void getByIdShouldThrowWhenOrderMissing() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getById(999L));
    }
}
