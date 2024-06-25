package com.ecommerce.domain.shoppingCart.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WishListResponse {
    private Integer wishListId;
    private Long productId;
    private String productName;
}
