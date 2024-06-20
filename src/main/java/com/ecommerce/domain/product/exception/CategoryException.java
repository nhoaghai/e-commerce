package com.ecommerce.domain.product.exception;

import com.ecommerce.common.exception.DomainException;

public class CategoryException extends DomainException {
    public CategoryException(String message){
        super(message);
    }
    public CategoryException(String message, Throwable cause){
        super(message, cause);
    }
}
