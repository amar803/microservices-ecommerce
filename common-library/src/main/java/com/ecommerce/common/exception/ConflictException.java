package com.ecommerce.common.exception;

public class ConflictException extends DomainException {

    public ConflictException(String message) {
        super("RESOURCE_CONFLICT", message, 409);
    }
}
