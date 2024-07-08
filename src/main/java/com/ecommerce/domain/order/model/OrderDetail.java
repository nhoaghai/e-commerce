package com.ecommerce.domain.order.model;

import com.ecommerce.domain.product.model.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_detail")
public class OrderDetail {
    @EmbeddedId
    private OrderDetailId orderDetailId;

    private OrderStatus orderDetailStatus;

    @Column(name = "product_quantity")
    private Integer productQuantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("orderId")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productId")
    @JsonIgnore
    private Product product;
}
