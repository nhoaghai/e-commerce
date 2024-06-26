package com.ecommerce.domain.product.repository;

import com.ecommerce.domain.member.model.Seller;
import com.ecommerce.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByProductNameContainingOrDescriptionContaining(String name, String des);

    List<Product> findAllByCategoryCategoryId(Long categoryId);

    Product findByProductId(Long productId);

    @Query("select p from Product p where p.sku= ?1")
    Product findByProductSku(String sku);

    List<Product> findAllBySellerSellerId(String sellerId);

    Product findBySellerSellerIdAndProductId(String sellerId, Long productId);
}
