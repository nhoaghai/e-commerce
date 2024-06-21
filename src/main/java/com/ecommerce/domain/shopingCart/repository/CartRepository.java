package com.ecommerce.domain.shopingCart.repository;

import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.shopingCart.model.ShoppingCart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findAllByMemberMemberId(String memberId);

    ShoppingCart findFirstByProductProductId(Long productId);

}
