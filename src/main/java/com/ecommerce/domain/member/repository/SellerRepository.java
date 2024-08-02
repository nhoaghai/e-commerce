package com.ecommerce.domain.member.repository;

import com.ecommerce.domain.member.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, String> {
    Optional<Seller> findByMemberMemberId(String memberId);
    Optional<Seller> findByShopName(String shopName);
}
