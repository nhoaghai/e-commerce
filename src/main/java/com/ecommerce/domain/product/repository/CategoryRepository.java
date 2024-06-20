package com.ecommerce.domain.product.repository;

import com.ecommerce.domain.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByCategoryNameContainingOrDescriptionContaining(String name, String des);

    Category findByCategoryId(Long categoryId);
}
