package com.ecommerce.common.api;

public record ErrorDetail(
        String field,
        String message
) {
}
