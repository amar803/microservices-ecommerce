package com.ecommerce.common.dto;

import java.math.BigDecimal;

public record OrderItemDto(
        Long productId,
        String sku,
        int quantity,
        BigDecimal unitPrice
) {
}
