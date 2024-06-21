package com.ecommerce.controller.member;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.order.dto.request.OrderRequest;
import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.serviceImpl.OrderServiceImpl;
import com.ecommerce.domain.shopingCart.dto.request.CartRequest;
import com.ecommerce.domain.shopingCart.dto.response.CartResponse;
import com.ecommerce.domain.shopingCart.serviceImpl.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member")
public class CartController {
    private final CartServiceImpl cartService;
    private final OrderServiceImpl orderService;

    @GetMapping("/carts")
    public ResponseEntity<List<CartResponse>> getAllCart(Pageable pageable){
        return ResponseEntity.ok(cartService.findAllCart());
    }

    @PostMapping("/carts")
    public ResponseEntity<CartResponse> addNewProductToCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.addNewProductIntoCart(cartRequest));
    }

    @PutMapping("/carts/add")
    public ResponseEntity<CartResponse> changeProductNumberInCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.changeProductNumberInCart(cartRequest));
    }

    @DeleteMapping("/carts/delete")
    public ResponseEntity<MessageResponse> deleteProductInCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok(cartService.deleteProductInCart(cartRequest));
    }

    @PostMapping("/carts/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestBody OrderRequest orderRequest){
        return ResponseEntity.ok(orderService.checkout(orderRequest));
    }
}
