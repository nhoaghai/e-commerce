package com.ecommerce.controller.member;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.shoppingCart.dto.request.WishListRequest;
import com.ecommerce.domain.shoppingCart.dto.response.WishListResponse;
import com.ecommerce.domain.shoppingCart.serviceImpl.WishListServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member/wishList")
@SecurityRequirement(name = "Bearer Authentication")
public class WishListController {
    private final WishListServiceImpl wishListService;

    @GetMapping("/show")
    public ResponseEntity<List<WishListResponse>> getAllWishItems(Pageable pageable) {
        return ResponseEntity.ok(wishListService.getAllWistlistItems());
    }

    @PostMapping("/add")
    public ResponseEntity<WishListResponse> addNewWishItem(@RequestBody WishListRequest wishListRequest) {
        return ResponseEntity.ok(wishListService.addProductToWishList(wishListRequest));
    }

    @DeleteMapping("/{wishListId}")
    public ResponseEntity<MessageResponse> deleteProductInWishList(@PathVariable Integer wishListId) {
        return ResponseEntity.ok(wishListService.removeProductFromWishListById(wishListId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponse> clearWishList() {
        return ResponseEntity.ok(wishListService.clearWishList());
    }

}
