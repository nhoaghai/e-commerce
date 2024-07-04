package com.ecommerce.controller.permitAll;

import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import com.ecommerce.domain.product.dto.response.ShopProductResponse;
import com.ecommerce.domain.product.serviceImpl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/products")
public class ProductController {

    private final ProductServiceImpl productService;
    @GetMapping
    public ResponseEntity<PageResponseDto<ProductResponse>> getAllProduct(Pageable pageable){
        return ResponseEntity.ok(productService.findAllProduct(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> findProductByNameOrDescription(@RequestParam("query") String keyword){
        return ResponseEntity.ok(productService.findAllByProductNameOrDes(keyword));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> findProductByCategoryId(@PathVariable Long categoryId){
        return ResponseEntity.ok(productService.findAllByCategoryId(categoryId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long productId){
        return ResponseEntity.ok(productService.findByProductId(productId));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> findProductBySku(@PathVariable String sku){
        return ResponseEntity.ok(productService.findByProductSku(sku));
    }

    @GetMapping("/shop/{shopName}")
    public ResponseEntity<List<ShopProductResponse>> findAllShopProduct(@PathVariable String shopName) {
        return ResponseEntity.ok(productService.findAllShopProduct(shopName));
    }
}
