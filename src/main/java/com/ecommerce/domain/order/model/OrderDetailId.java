package com.ecommerce.domain.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class OrderDetailId implements Serializable {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "order_id")
    private Long orderId;

}
