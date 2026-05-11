package com.ecommerce.inventoryservice.web;

public record InventoryItemView(
        Long id,
        Long productId,
        String sku,
        int availableQuantity,
        int reservedQuantity
) {
}

