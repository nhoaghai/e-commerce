package com.ecommerce.domain.product.repository;

import com.ecommerce.domain.product.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByCategoryNameContainingOrDescriptionContaining(String name, String des);

    Category findByCategoryId(Long categoryId);

    @Query(value = "SELECT c from Category c where c.isActive = true")
    Page<Category> findCategoriesByActive(Pageable pageable);
//    @Query(value = "select  C from Category  C where C.active = true")
//    Page<Category> findProductActive(Pageable pageable);
}
