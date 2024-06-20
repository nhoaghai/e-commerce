package com.ecommerce.domain.product.service;

import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.product.dto.request.CategoryRequest;
import com.ecommerce.domain.product.dto.response.CategoryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    PageResponseDto<CategoryResponse> findAllCategory(Pageable pageable);

    List<CategoryResponse> getAllCategoryByNameOrDes(String keyword);

    CategoryResponse findCategoryById(Long categoryId);

    CategoryResponse addNewCategory(CategoryRequest categoryRequest);
}
