package com.ecommerce.domain.shoppingCart.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WishListRequest {
    @NotBlank
    private Long productId;
    @NotBlank
    private String memberId;
}
