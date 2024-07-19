package com.ecommerce.domain.product.serviceImpl;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.product.dto.request.CategoryRequest;
import com.ecommerce.domain.product.dto.response.CategoryResponse;
import com.ecommerce.domain.product.exception.CategoryException;
import com.ecommerce.domain.product.model.Category;
import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.product.repository.CategoryRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Override
    public PageResponseDto<CategoryResponse> findAllCategory(Pageable pageable) {
        Page<Category> page = categoryRepository.findCategoriesByActive(pageable);
        if (page.isEmpty()) {
            throw CategoryException.notFound("No category found");
        }

        List<CategoryResponse> data = page.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .toList();
        return PageResponseDto.<CategoryResponse>builder()
                .data(data)
                .totalPage(page.getTotalPages())
                .pageNumber(page.getNumber())
                .size(page.getSize())
                .sort(page.getSort().toString())
                .build();
    }

    @Override
    public List<CategoryResponse> getAllCategoryByNameOrDes(String keyword) {
        List<Category> categories = categoryRepository.findAllByCategoryNameContainingOrDescriptionContaining(keyword, keyword);

        if (categories.isEmpty()){
            throw CategoryException.notFound("No category found matching the search criteria");
        } else {
            return categories.stream()
                    .map(category -> modelMapper.map(category, CategoryResponse.class))
                    .toList();
        }
    }

    @Override
    public CategoryResponse findCategoryById(Long categoryId) {
        Category category = categoryRepository.findByCategoryId(categoryId);
        if (category == null){
            throw CategoryException.notFound("Could not found category with id");
        } else {
            return modelMapper.map(category, CategoryResponse.class);
        }
    }

    @Override
    public CategoryResponse addNewCategory(CategoryRequest categoryRequest) {
        if (categoryRequest.getCategoryId() == null) {
            throw CategoryException.badRequest("Please enter category ID");
        }
        if (categoryRequest.getCategoryName() == null) {
            throw CategoryException.badRequest("Please enter category name");
        }
        if (categoryRequest.getDescription() == null) {
            throw CategoryException.badRequest("Please enter category description");
        }

        Category category = modelMapper.map(categoryRequest, Category.class);
        category.setActive(true);
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponse.class);
    }


    @Override
    public List<CategoryResponse> updateCategory(List<CategoryRequest> categoryRequest) {
        List<CategoryResponse> responses = new ArrayList<>();
        for(CategoryRequest request: categoryRequest) {
            Category category = categoryRepository.findByCategoryId(request.getCategoryId());
            if (category == null) {
                throw new DomainException("This category does not exist");
            }
            if (Objects.equals(category.getCategoryName(), "") || Objects.equals(category.getDescription(), "")) {
                throw new CategoryException("Please provide category name and description");
            }
            categoryRepository.save(modelMapper.map(categoryRequest, Category.class));
            responses.add(modelMapper.map(category, CategoryResponse.class));
        }
        return responses;
    }

    public CategoryResponse deleteCategory(CategoryRequest categoryRequest) {
        Category category = categoryRepository.findByCategoryId(categoryRequest.getCategoryId());
        if(category == null) {
            throw new CategoryException("This category does not exist");
        }
        List<Product> products = productRepository.findAllByCategoryCategoryId(category.getCategoryId());
        if(!products.isEmpty()) {
            throw new CategoryException("There are products in this category");
        }
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryResponse.class);
    }

}
