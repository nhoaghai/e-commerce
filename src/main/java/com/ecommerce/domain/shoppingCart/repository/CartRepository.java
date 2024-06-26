package com.ecommerce.domain.shoppingCart.repository;

import com.ecommerce.domain.shoppingCart.model.ShoppingCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, Long> {
    Page<ShoppingCart> findAllByMemberMemberId(String memberId, Pageable pageable);

    ShoppingCart findFirstByProductProductId(Long productId);

}
