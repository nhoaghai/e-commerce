package com.ecommerce.domain.shoppingCart.exception;

import com.ecommerce.common.exception.DomainException;

public class WishListException extends DomainException{
    public WishListException(String message){
        super(message);
    }
    public WishListException(String message, Throwable cause){
        super(message, cause);
    }
}
