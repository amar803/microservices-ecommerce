package com.ecommerce.common.exception;

import com.ecommerce.common.error.ErrorCode;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_ERROR, message);
    }
}
