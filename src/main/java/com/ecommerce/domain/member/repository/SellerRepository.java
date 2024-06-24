package com.ecommerce.domain.member.repository;

import com.ecommerce.domain.member.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, String> {
    Seller findByMemberMemberId(String memberId);
}
