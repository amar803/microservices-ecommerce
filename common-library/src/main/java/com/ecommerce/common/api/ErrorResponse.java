package com.ecommerce.common.api;

import com.ecommerce.common.error.ErrorCode;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        int status,
        List<ErrorDetail> details,
        Instant timestamp
) {
    public static ErrorResponse of(String code, String message, int status) {
        return new ErrorResponse(code, message, status, List.of(), Instant.now());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.getCode(), message, errorCode.getHttpStatus(), List.of(), Instant.now());
    }

    public static ErrorResponse of(String code, String message, int status, List<ErrorDetail> details) {
        return new ErrorResponse(code, message, status, details, Instant.now());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, List<ErrorDetail> details) {
        return new ErrorResponse(errorCode.getCode(), message, errorCode.getHttpStatus(), details, Instant.now());
    }
}
