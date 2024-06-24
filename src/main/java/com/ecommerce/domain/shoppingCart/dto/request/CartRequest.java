package com.ecommerce.domain.shoppingCart.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartRequest {
    @NotBlank
    private Integer productQuantity;
    @NotBlank
    private Long productId;
    @NotBlank
    private String memberId;
}
