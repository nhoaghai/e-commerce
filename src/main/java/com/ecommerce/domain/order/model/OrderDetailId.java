package com.ecommerce.domain.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class OrderDetailId implements Serializable {
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "order_id")
    private Long orderId;
}
