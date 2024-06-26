package com.ecommerce.domain.shoppingCart.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.shoppingCart.dto.request.WishListRequest;
import com.ecommerce.domain.shoppingCart.dto.response.WishListResponse;
import org.springframework.data.domain.Pageable;

public interface WishListService {
    PageResponseDto<WishListResponse> getAllWistlistItems(Pageable pageable);

    WishListResponse addProductToWishList(WishListRequest request);

    MessageResponse removeProductFromWishListById(Integer wishListId);

    MessageResponse clearWishList();

}
