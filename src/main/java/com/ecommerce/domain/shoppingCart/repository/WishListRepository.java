package com.ecommerce.domain.shoppingCart.repository;

import com.ecommerce.domain.shoppingCart.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findAllByMemberMemberId(String memberId);

    WishList findFirstByProductProductId(Long productId);
}
