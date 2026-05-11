package com.ecommerce.common.exception;

public class NotFoundException extends DomainException {

    public NotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message, 404);
    }
}
