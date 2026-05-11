package com.ecommerce.common.error;

public enum ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR", 400),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", 404),
    RESOURCE_CONFLICT("RESOURCE_CONFLICT", 409),
    UNAUTHORIZED("UNAUTHORIZED", 401),
    FORBIDDEN("FORBIDDEN", 403),
    DOWNSTREAM_SERVICE_ERROR("DOWNSTREAM_SERVICE_ERROR", 502),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", 500);

    private final String code;
    private final int httpStatus;

    ErrorCode(String code, int httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}

