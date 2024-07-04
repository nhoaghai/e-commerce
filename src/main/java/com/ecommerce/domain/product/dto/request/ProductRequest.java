package com.ecommerce.domain.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String categoryName;
    private String productName;
    private Integer stockQuantity;
    private BigDecimal unitPrice;
    private String description;
    private Integer discount;
    private String imageUrl;
}
