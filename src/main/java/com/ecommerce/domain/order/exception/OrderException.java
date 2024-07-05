package com.ecommerce.domain.order.exception;

import com.ecommerce.common.exception.DomainException;

public class OrderException extends DomainException {
    public OrderException(String message){
        super(message);
    }
    public OrderException(String message, Throwable cause){
        super(message, cause);
    }
}
