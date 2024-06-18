package com.ecommerce.domain.order.model;

import com.ecommerce.domain.product.model.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_detail")
public class OrderDetail {
    @EmbeddedId
    private OrderDetailId orderDetailId;

    @Column(name = "note")
    private String note;

    @Column(name = "receive_name")
    private String receiveName;

    @Column(name = "receive_address")
    private String receiveAddress;

    @Column(name = "receive_phone", columnDefinition = "varchar(15)")
    private String receivePhone;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "receive_at")
    private LocalDateTime receiveAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JsonIgnore
    private Product product;
}
