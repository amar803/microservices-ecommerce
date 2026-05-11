package com.ecommerce.common.exception;

import com.ecommerce.common.error.ErrorCode;

public class BusinessException extends DomainException {

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

