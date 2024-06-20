package com.ecommerce.domain.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String sku;
    private String productName;
    private Integer stockQuantity;
    private BigDecimal unitPrice;
    private String description;
    private Integer discount;
    private String imageUrl;
    private boolean isActive;
    private String categoryName;
    private String shopName;
}
