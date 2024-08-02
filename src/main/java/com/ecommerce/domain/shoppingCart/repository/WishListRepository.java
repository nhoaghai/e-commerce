package com.ecommerce.domain.shoppingCart.repository;

import com.ecommerce.domain.shoppingCart.model.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Integer> {
    Page<WishList> findAllByMemberMemberId(String memberId, Pageable pageable);

    Optional<WishList> findFirstByProductProductId(Long productId);
}
