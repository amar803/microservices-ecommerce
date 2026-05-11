package com.ecommerce.common.dto;

import java.math.BigDecimal;

public record PaymentDto(
        Long id,
        Long orderId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String idempotencyKey
) {
}
