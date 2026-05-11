package com.ecommerce.common.api;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorResponse error,
        Instant timestamp
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> fail(ErrorResponse error) {
        return new ApiResponse<>(false, null, error, Instant.now());
    }
}
