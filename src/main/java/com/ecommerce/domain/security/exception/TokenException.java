package com.ecommerce.domain.security.exception;

import com.ecommerce.common.exception.DomainException;

public class TokenException extends DomainException {
    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
