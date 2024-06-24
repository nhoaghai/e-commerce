package com.ecommerce.domain.shoppingCart.repository;

import com.ecommerce.domain.shoppingCart.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findAllByMemberMemberId(String memberId);

    ShoppingCart findFirstByProductProductId(Long productId);

}
