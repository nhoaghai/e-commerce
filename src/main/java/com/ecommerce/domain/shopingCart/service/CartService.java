package com.ecommerce.domain.shopingCart.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.shopingCart.dto.request.CartRequest;
import com.ecommerce.domain.shopingCart.dto.response.CartResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {
    List<CartResponse> findAllCart();

    CartResponse addNewProductIntoCart(CartRequest cartRequest);

    CartResponse changeProductNumberInCart(CartRequest cartRequest);

    MessageResponse deleteProductInCart(CartRequest cartRequest);
}
