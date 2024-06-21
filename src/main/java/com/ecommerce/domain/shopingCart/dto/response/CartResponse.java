package com.ecommerce.domain.shopingCart.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CartResponse {
    private Integer productQuantity;
    private Long productId;
    private String productName;
}
