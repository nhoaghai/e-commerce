package com.ecommerce.domain.product.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.product.dto.request.ProductRequest;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import com.ecommerce.domain.product.dto.response.ShopProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    PageResponseDto<ProductResponse> findAllProduct(Pageable pageable);

    List<ProductResponse> findAllByProductNameOrDes(String keyword);

    List<ProductResponse> findAllByCategoryId(Long categoryId);

    ProductResponse findByProductId(Long productId);

    ProductResponse findByProductSku(String sku);

    List<ShopProductResponse> findAllShopProduct(String shopName);

    MessageResponse addNewProduct(ProductRequest productRequest);

    MessageResponse updateProduct(ProductRequest productRequest);

    MessageResponse deleteByProductId(Long productId);

    List<ProductResponse> findAllBySellerId(String sellerId);
}
