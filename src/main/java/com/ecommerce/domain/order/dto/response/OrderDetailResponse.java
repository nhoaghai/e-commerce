package com.ecommerce.domain.order.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDetailResponse {
    private Integer productQuantity;

    private String productName;

    private BigDecimal productPrice;

    private String productDescription;

    private String productImageUrl;

    private String productCategory;

    private Integer productDiscount;

}
