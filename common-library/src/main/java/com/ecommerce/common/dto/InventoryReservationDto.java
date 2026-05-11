package com.ecommerce.common.dto;

public record InventoryReservationDto(
        Long productId,
        String sku,
        int requestedQuantity,
        int reservedQuantity,
        boolean reserved
) {
}
