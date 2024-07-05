package com.ecommerce.controller.seller;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.member.dto.request.SellerProductRequest;
import com.ecommerce.domain.member.dto.request.SellerSignUpRequest;
import com.ecommerce.domain.member.serviceImpl.SellerServiceImpl;
import com.ecommerce.domain.product.dto.request.ProductRequest;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import com.ecommerce.domain.order.dto.response.SellerOrderDetailResponse;
import com.ecommerce.domain.order.model.OrderStatus;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/product")
    public ResponseEntity<PageResponseDto<ProductResponse>> allSellingProduct(Pageable pageable) {
        return ResponseEntity.ok(sellerService.findAllSellingProduct(pageable));
    }

    @GetMapping("/product/{sku}")
    public ResponseEntity<ProductResponse> getSellingProduct(@PathVariable String sku) {
        return ResponseEntity.ok(sellerService.findSellingProduct(sku));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> editSellerProduct(@PathVariable Long productId,
                                                             @RequestBody SellerProductRequest request) {
        return ResponseEntity.ok(sellerService.editProduct(productId, request));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<MessageResponse> deleteSellerProduct(@PathVariable Long productId) {
        return  ResponseEntity.ok(sellerService.deleteProduct(productId));
    }

    @GetMapping("/order")
    public ResponseEntity<PageResponseDto<SellerOrderDetailResponse>> getSellerOrders
            (@SortDefault(sort = "orderId") Pageable pageable) {
        return ResponseEntity.ok(sellerService.getAllOrders(pageable));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<SellerOrderDetailResponse>> getSellerOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(sellerService.getOrderById(orderId));
    }

    @GetMapping("/order/status/{status}")
    public ResponseEntity<PageResponseDto<SellerOrderDetailResponse>> getOrdersByStatus
            (@PathVariable OrderStatus status, @SortDefault(sort = "productDiscount", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(sellerService.getOrderByStatus(status, pageable));
    }

    @GetMapping("/revenue-over-time")
    public ResponseEntity<MessageResponse> getRevenueOverTime
            (@RequestParam String startDateStr,
             @RequestParam String endDateStr) {
        return ResponseEntity.ok(sellerService.getRevenueOverTime(startDateStr, endDateStr));
    }
}
