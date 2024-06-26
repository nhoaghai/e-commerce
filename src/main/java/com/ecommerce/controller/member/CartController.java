package com.ecommerce.controller.member;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.order.dto.request.OrderRequest;
import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.serviceImpl.OrderServiceImpl;
import com.ecommerce.domain.shoppingCart.dto.request.CartRequest;
import com.ecommerce.domain.shoppingCart.dto.response.CartResponse;
import com.ecommerce.domain.shoppingCart.serviceImpl.CartServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member/carts")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {
    private final CartServiceImpl cartService;
    private final OrderServiceImpl orderService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<CartResponse>> findAllCart(
            @SortDefault(sort = "shoppingCartId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(cartService.findAllCart(pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addNewProductIntoCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.addNewProductIntoCart(cartRequest));
    }

    @PutMapping("/change-quantity")
    public ResponseEntity<CartResponse> changeProductQuantityInCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.changeProductQuantityInCart(cartRequest));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<MessageResponse> deleteProductInCart(@PathVariable Integer cartId) {
        return ResponseEntity.ok(cartService.deleteProductInCart(cartId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponse> clearAllProductsInCart() {
        return ResponseEntity.ok(cartService.clearAllProductsInCart());
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestBody OrderRequest orderRequest){
        return ResponseEntity.ok(orderService.checkout(orderRequest));
    }
}
