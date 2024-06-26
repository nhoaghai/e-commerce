package com.ecommerce.domain.member.model;

import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.security.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seller")
public class Seller {

    @Id
    @Column(name = "member_id")
    private String sellerId;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    @MapsId
    private Member member;

    @OneToMany(mappedBy = "seller")
    private List<Product> products;
}
