package com.ecommerce.domain.member.exception;

import com.ecommerce.common.exception.DomainException;

public class AddressException extends DomainException {
    public AddressException(String message){
        super(message);
    }
    public AddressException(String message, Throwable cause){
        super(message, cause);
    }
}
