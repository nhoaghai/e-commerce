package com.ecommerce.domain.product.serviceImpl;

import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.product.dto.request.CategoryRequest;
import com.ecommerce.domain.product.dto.response.CategoryResponse;
import com.ecommerce.domain.product.exception.CategoryException;
import com.ecommerce.domain.product.model.Category;
import com.ecommerce.domain.product.repository.CategoryRepository;
import com.ecommerce.domain.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    @Override
    public PageResponseDto<CategoryResponse> findAllCategory(Pageable pageable) {
        Page<Category> page = categoryRepository.findCategoriesByActive(pageable);
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
        }else {
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
        }else {
            return modelMapper.map(category, CategoryResponse.class);
        }
    }

    @Override
    public CategoryResponse addNewCategory(CategoryRequest categoryRequest) {
        Category category = categoryRepository.findByCategoryId(categoryRequest.getCategoryId());
        if (category == null){
            //add new
            categoryRepository.save(modelMapper.map(categoryRequest, Category.class));
        }else {
            //update
            category.setCategoryName(categoryRequest.getCategoryName());
            category.setDescription(categoryRequest.getDescription());
        }
        return modelMapper.map(category, CategoryResponse.class);
    }
}
