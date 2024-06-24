package com.ecommerce.domain.product.model;

import com.ecommerce.domain.member.model.Seller;
import com.ecommerce.domain.shopingCart.model.ShoppingCart;
import com.ecommerce.domain.shopingCart.model.WishList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description")
    private String description;

    @Column(name = "unit_price", precision = 10, scale = 3)
    private BigDecimal unitPrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "discount")
    private Integer discount;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime update_at;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @JsonIgnoreProperties({"categoryName", "description"})
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ShoppingCart> shoppingCarts;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<WishList> wishLists;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", referencedColumnName = "member_id")
    @JsonIgnoreProperties("shopName")
    private Seller seller;
}
