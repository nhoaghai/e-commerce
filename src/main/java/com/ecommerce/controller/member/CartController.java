package com.ecommerce.controller.member;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.order.dto.request.OrderRequest;
import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.serviceImpl.OrderServiceImpl;
import com.ecommerce.domain.shoppingCart.dto.request.CartRequest;
import com.ecommerce.domain.shoppingCart.dto.response.CartResponse;
import com.ecommerce.domain.shoppingCart.serviceImpl.CartServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member/carts")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {
    private final CartServiceImpl cartService;
    private final OrderServiceImpl orderService;

    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCart(Pageable pageable){
        return ResponseEntity.ok(cartService.findAllCart());
    }

    @PostMapping
    public ResponseEntity<CartResponse> addNewProductToCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.addNewProductIntoCart(cartRequest));
    }

    @PutMapping("/add")
    public ResponseEntity<CartResponse> changeProductNumberInCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.changeProductNumberInCart(cartRequest));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<MessageResponse> deleteProductInCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.deleteProductInCart(cartRequest));
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
