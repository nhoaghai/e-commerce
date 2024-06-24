package com.ecommerce.domain.product.exception;

import com.ecommerce.common.exception.DomainException;

public class ProductException extends DomainException {
    public ProductException(String message){
        super(message);
    }
    public ProductException(String message, Throwable cause){
        super(message, cause);
    }
}
