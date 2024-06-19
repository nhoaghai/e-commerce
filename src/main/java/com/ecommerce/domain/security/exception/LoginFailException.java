package com.ecommerce.domain.security.exception;

public class LoginFailException extends RuntimeException{
    public LoginFailException(String message){
        super(message);
    }
}
