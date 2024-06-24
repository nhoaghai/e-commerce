package com.ecommerce.domain.member.exception;

import com.ecommerce.common.exception.DomainException;

public class SellerException extends DomainException {
    public SellerException(String message){
        super(message);
    }
    public SellerException(String message, Throwable cause){
        super(message, cause);
    }
}
