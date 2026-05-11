package com.ecommerce.common.exception;

import com.ecommerce.common.error.ErrorCode;

public abstract class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String code;
    private final int status;

    protected DomainException(String code, String message, int status) {
        super(message);
        this.errorCode = null;
        this.code = code;
        this.status = status;
    }

    protected DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.code = errorCode.getCode();
        this.status = errorCode.getHttpStatus();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
