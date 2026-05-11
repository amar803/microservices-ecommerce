package com.ecommerce.common.error;

import com.ecommerce.common.api.ErrorDetail;

import java.time.Instant;
import java.util.List;

public record GlobalExceptionDto(
        String code,
        String message,
        int status,
        String path,
        String correlationId,
        List<ErrorDetail> details,
        Instant timestamp
) {
    public static GlobalExceptionDto of(
            ErrorCode errorCode,
            String message,
            String path,
            String correlationId,
            List<ErrorDetail> details
    ) {
        return new GlobalExceptionDto(
                errorCode.getCode(),
                message,
                errorCode.getHttpStatus(),
                path,
                correlationId,
                details,
                Instant.now()
        );
    }
}

