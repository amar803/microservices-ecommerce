package com.ecommerce.common.exception;

import com.ecommerce.common.error.ErrorCode;

public class DownstreamServiceException extends BusinessException {

    public DownstreamServiceException(String message) {
        super(ErrorCode.DOWNSTREAM_SERVICE_ERROR, message);
    }

    public DownstreamServiceException(String serviceName, String message) {
        super(ErrorCode.DOWNSTREAM_SERVICE_ERROR, serviceName + ": " + message);
    }
}

