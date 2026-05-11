package com.ecommerce.common.exception;

import com.ecommerce.common.error.ErrorCode;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(ErrorCode.RESOURCE_CONFLICT, message);
    }
}
