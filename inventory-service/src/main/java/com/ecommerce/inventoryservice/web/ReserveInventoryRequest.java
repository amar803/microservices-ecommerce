package com.ecommerce.inventoryservice.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReserveInventoryRequest(
        @NotNull Long productId,
        @NotBlank String sku,
        @Min(1) int quantity
) {
}

