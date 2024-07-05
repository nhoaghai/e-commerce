package com.ecommerce.domain.member.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.member.dto.request.SellerProductRequest;
import com.ecommerce.domain.member.dto.request.SellerSignUpRequest;
import com.ecommerce.domain.product.dto.request.ProductRequest;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import com.ecommerce.domain.order.dto.response.SellerOrderDetailResponse;
import com.ecommerce.domain.order.model.OrderStatus;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SellerService {
    MessageResponse sellerSignUp(SellerSignUpRequest sellerSignUpRequest);

    MessageResponse sellerSignIn();

    PageResponseDto<ProductResponse> findAllSellingProduct(Pageable pageable);

    ProductResponse findSellingProduct(String sku);

    ProductResponse addProduct(ProductRequest request);


    ProductResponse editProduct(Long productId, SellerProductRequest request);

    MessageResponse deleteProduct(Long productId);

    PageResponseDto<SellerOrderDetailResponse> getAllOrders(Pageable pageable);

    List<SellerOrderDetailResponse> getOrderById(Long orderId);

    PageResponseDto<SellerOrderDetailResponse> getOrderByStatus(OrderStatus status, Pageable pageable);

    MessageResponse getRevenueOverTime(String startDate, String endDate);
}
