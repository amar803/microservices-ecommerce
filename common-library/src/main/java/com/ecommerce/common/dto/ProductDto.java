package com.ecommerce.common.dto;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        boolean active
) {
}
