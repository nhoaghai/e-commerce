package com.ecommerce.controller.admin;

import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.product.dto.request.CategoryRequest;
import com.ecommerce.domain.product.dto.response.CategoryResponse;
import com.ecommerce.domain.product.serviceImpl.CategoryServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/admin")
@SecurityRequirement(name = "Bearer Authentication")

public class AdminController {
    private final CategoryServiceImpl categoryService;

    @GetMapping("/category")
    public ResponseEntity<PageResponseDto<CategoryResponse>> getAllCategory(@SortDefault(sort = "categoryId") Pageable pageable){
        return ResponseEntity.ok(categoryService.findAllCategory(pageable));
    }

    @GetMapping("/category/search")
    public ResponseEntity<List<CategoryResponse>> getAllCategoryByNameOrDescription(@RequestParam("query") String keyword){
        return ResponseEntity.ok(categoryService.getAllCategoryByNameOrDes(keyword));
    }

    @PostMapping("/category/new-category")
    public ResponseEntity<CategoryResponse> addNewCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.addNewCategory(request));
    }

    @DeleteMapping("/category/delete-category")
    public ResponseEntity<CategoryResponse> deleteCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.deleteCategory(request));
    }

}
