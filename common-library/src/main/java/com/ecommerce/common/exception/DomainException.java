package com.ecommerce.common.exception;

public abstract class DomainException extends RuntimeException {

    private final String code;
    private final int status;

    protected DomainException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
