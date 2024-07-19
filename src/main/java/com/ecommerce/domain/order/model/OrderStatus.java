package com.ecommerce.domain.order.model;

/*
        => DENY
WAITING => CONFIRM => DELIVERY => SUCCESS
        => CANCEL
 */

public enum OrderStatus {
    WAITING, CONFIRM, DELIVERY, SUCCESS, CANCEL, DENIED
}