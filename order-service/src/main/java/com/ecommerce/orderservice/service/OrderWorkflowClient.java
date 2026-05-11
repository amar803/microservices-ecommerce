package com.ecommerce.orderservice.service;

import com.ecommerce.common.api.ApiResponse;
import com.ecommerce.common.dto.NotificationChannel;
import com.ecommerce.common.dto.NotificationRequestDto;
import com.ecommerce.common.dto.PaymentDto;
import com.ecommerce.common.dto.PaymentStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Component
public class OrderWorkflowClient {

    private static final ParameterizedTypeReference<ApiResponse<PaymentDto>> PAYMENT_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestTemplate restTemplate;

    public OrderWorkflowClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean reserveInventory(Long productId, String sku, int quantity) {
        Map<String, Object> request = Map.of(
                "productId", productId,
                "sku", sku,
                "quantity", quantity
        );

        ResponseEntity<ApiResponse<Map<String, Object>>> response = restTemplate.exchange(
                "http://inventory-service/api/v1/inventory/reserve",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {
                }
        );

        ApiResponse<Map<String, Object>> body = response.getBody();
        if (body == null || !body.success() || body.data() == null) {
            return false;
        }

        Object reserved = body.data().get("reserved");
        return reserved instanceof Boolean b && b;
    }

    public void releaseInventory(Long productId, String sku, int quantity) {
        Map<String, Object> request = Map.of(
                "productId", productId,
                "sku", sku,
                "quantity", quantity
        );

        restTemplate.postForEntity(
                "http://inventory-service/api/v1/inventory/release",
                request,
                Object.class
        );
    }

    public PaymentDto authorizePayment(Long orderId, BigDecimal amount, String idempotencyKey) {
        Map<String, Object> request = Map.of(
                "orderId", orderId,
                "amount", amount,
                "currency", "USD",
                "idempotencyKey", idempotencyKey
        );

        ResponseEntity<ApiResponse<PaymentDto>> response = restTemplate.exchange(
                "http://payment-service/api/v1/payments",
                HttpMethod.POST,
                new HttpEntity<>(request),
                PAYMENT_RESPONSE
        );

        ApiResponse<PaymentDto> body = response.getBody();
        if (body == null || !body.success() || body.data() == null) {
            throw new OrchestrationException("Payment authorization failed");
        }

        return body.data();
    }

    public PaymentDto capturePayment(Long paymentId) {
        ResponseEntity<ApiResponse<PaymentDto>> response = restTemplate.exchange(
                "http://payment-service/api/v1/payments/{paymentId}/capture",
                HttpMethod.POST,
                HttpEntity.EMPTY,
                PAYMENT_RESPONSE,
                paymentId
        );

        ApiResponse<PaymentDto> body = response.getBody();
        if (body == null || !body.success() || body.data() == null) {
            throw new OrchestrationException("Payment capture failed");
        }

        if (body.data().status() != PaymentStatus.CAPTURED) {
            throw new OrchestrationException("Payment is not in CAPTURED state");
        }

        return body.data();
    }

    public void sendOrderNotification(Long userId, Long orderId, BigDecimal amount) {
        NotificationRequestDto request = new NotificationRequestDto(
                userId,
                NotificationChannel.EMAIL,
                "order-confirmed",
                "Order confirmed",
                "Order " + orderId + " is confirmed with amount " + amount,
                Map.of("orderId", String.valueOf(orderId))
        );

        restTemplate.postForEntity(
                "http://notification-service/api/v1/notifications",
                request,
                Object.class
        );
    }

    public void publishReportEvent(String eventType) {
        Map<String, String> request = Map.of("eventType", Objects.requireNonNull(eventType));
        restTemplate.postForEntity(
                "http://report-service/api/v1/reports/events",
                request,
                Object.class
        );
    }
}

