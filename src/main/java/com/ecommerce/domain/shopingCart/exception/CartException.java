package com.ecommerce.domain.shopingCart.exception;

import com.ecommerce.common.exception.DomainException;

public class CartException extends DomainException {
    public CartException(String message){
        super(message);
    }
    public CartException(String message, Throwable cause){
        super(message, cause);
    }
}
