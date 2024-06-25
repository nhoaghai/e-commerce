package com.ecommerce.domain.shoppingCart.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.shoppingCart.dto.request.CartRequest;
import com.ecommerce.domain.shoppingCart.dto.response.CartResponse;

import java.util.List;

public interface CartService {
    List<CartResponse> findAllCart();

    CartResponse addNewProductIntoCart(CartRequest cartRequest);

    CartResponse changeProductQuantityInCart(CartRequest cartRequest);

    MessageResponse deleteProductInCart(Integer cardId);

    MessageResponse clearAllProductsInCart();
}
