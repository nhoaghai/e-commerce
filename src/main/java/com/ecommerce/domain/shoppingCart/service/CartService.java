package com.ecommerce.domain.shoppingCart.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.shoppingCart.dto.request.CartRequest;
import com.ecommerce.domain.shoppingCart.dto.response.CartResponse;
import org.springframework.data.domain.Pageable;

public interface CartService {
    PageResponseDto<CartResponse> findAllCart(Pageable pageable);

    CartResponse addNewProductIntoCart(CartRequest cartRequest);

    CartResponse changeProductQuantityInCart(CartRequest cartRequest);

    MessageResponse deleteProductInCart(Integer cardId);

    MessageResponse clearAllProductsInCart();
}
