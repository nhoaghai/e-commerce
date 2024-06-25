package com.ecommerce.domain.shoppingCart.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.shoppingCart.dto.request.WishListRequest;
import com.ecommerce.domain.shoppingCart.dto.response.WishListResponse;


import java.util.List;

public interface WishListService {
    List<WishListResponse> getAllWistlistItems();

    WishListResponse addProductToWishList(WishListRequest request);

    MessageResponse removeProductFromWishListById(Integer wishListId);

    MessageResponse clearWishList();
}
