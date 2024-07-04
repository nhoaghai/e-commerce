package com.ecommerce.domain.member.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.member.dto.request.SellerSignUpRequest;
import com.ecommerce.domain.product.dto.request.ProductRequest;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

public interface SellerService {
    MessageResponse sellerSignUp(SellerSignUpRequest sellerSignUpRequest);

    MessageResponse sellerSignIn();

    PageResponseDto<ProductResponse> findAllSellingProduct(Pageable pageable);

    ProductResponse findSellingProduct(String sku);

    ProductResponse addProduct(ProductRequest request);

}
