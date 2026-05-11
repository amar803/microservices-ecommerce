package com.ecommerce.common.error;

import com.ecommerce.common.api.ErrorResponse;
import com.ecommerce.common.exception.ConflictException;
import com.ecommerce.common.exception.DownstreamServiceException;
import com.ecommerce.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorModelTest {

    @Test
    void notFoundExceptionShouldExposeEnumDerivedCodeAndStatus() {
        NotFoundException ex = new NotFoundException("missing");

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        assertEquals("RESOURCE_NOT_FOUND", ex.getCode());
        assertEquals(404, ex.getStatus());
    }

    @Test
    void conflictExceptionShouldExposeEnumDerivedCodeAndStatus() {
        ConflictException ex = new ConflictException("exists");

        assertEquals(ErrorCode.RESOURCE_CONFLICT, ex.getErrorCode());
        assertEquals("RESOURCE_CONFLICT", ex.getCode());
        assertEquals(409, ex.getStatus());
    }

    @Test
    void downstreamServiceExceptionShouldUseExpectedErrorCode() {
        DownstreamServiceException ex = new DownstreamServiceException("payment-service", "timeout");

        assertEquals(ErrorCode.DOWNSTREAM_SERVICE_ERROR, ex.getErrorCode());
        assertEquals("DOWNSTREAM_SERVICE_ERROR", ex.getCode());
        assertEquals(502, ex.getStatus());
        assertEquals("payment-service: timeout", ex.getMessage());
    }

    @Test
    void errorResponseShouldSupportErrorCodeFactory() {
        ErrorResponse response = ErrorResponse.of(ErrorCode.VALIDATION_ERROR, "bad request");

        assertEquals("VALIDATION_ERROR", response.code());
        assertEquals(400, response.status());
        assertEquals("bad request", response.message());
    }
}

