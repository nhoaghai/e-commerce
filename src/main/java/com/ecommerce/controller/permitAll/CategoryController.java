package com.ecommerce.controller.permitAll;

import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.product.dto.response.CategoryResponse;
import com.ecommerce.domain.product.model.Category;
import com.ecommerce.domain.product.serviceImpl.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/categories")
public class CategoryController {

    private final CategoryServiceImpl categoryService;
    @GetMapping
    public ResponseEntity<PageResponseDto<CategoryResponse>> getAllCategory(Pageable pageable){
        return ResponseEntity.ok(categoryService.findAllCategory(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> getAllCategoryByNameOrDescription(@RequestParam("query") String keyword){
        return ResponseEntity.ok(categoryService.getAllCategoryByNameOrDes(keyword));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> findCategoryById(@PathVariable Long categoryId){
        return ResponseEntity.ok(categoryService.findCategoryById(categoryId));
    }
}
