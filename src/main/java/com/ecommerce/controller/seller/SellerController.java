package com.ecommerce.controller.seller;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.member.dto.request.SellerSignUpRequest;
import com.ecommerce.domain.member.serviceImpl.SellerServiceImpl;
import com.ecommerce.domain.product.dto.request.ProductRequest;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/seller")
@SecurityRequirement(name = "Bearer Authentication")
public class SellerController {

    private final SellerServiceImpl sellerService;
    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponse> sellerRegister(@RequestBody SellerSignUpRequest sellerSignUpRequest){
        return ResponseEntity.ok(sellerService.sellerSignUp(sellerSignUpRequest));
    }

    @GetMapping("/sign-in")
    public ResponseEntity<MessageResponse> sellerSignIn(){
        return ResponseEntity.ok(sellerService.sellerSignIn());
    }

    @PostMapping("/product/new-product")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(sellerService.addProduct(request));
    }
}
