package com.ecommerce.orderservice.service;

import com.ecommerce.common.exception.DomainException;

public class OrchestrationException extends DomainException {

    public OrchestrationException(String message) {
        super("DOWNSTREAM_ORCHESTRATION_ERROR", message, 502);
    }
}

