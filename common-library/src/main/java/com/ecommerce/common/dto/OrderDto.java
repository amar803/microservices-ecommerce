package com.ecommerce.common.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        Long id,
        Long userId,
        List<OrderItemDto> items,
        BigDecimal totalAmount,
        OrderStatus status
) {
}
