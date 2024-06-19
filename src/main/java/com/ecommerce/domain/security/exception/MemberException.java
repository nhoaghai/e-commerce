package com.ecommerce.domain.security.exception;

import com.ecommerce.common.exception.DomainException;

public class MemberException extends DomainException {
    public  MemberException(String message){
        super(message);
    }
    public MemberException(String message, Throwable cause){
        super(message, cause);
    }
}
