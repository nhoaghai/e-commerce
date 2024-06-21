package com.ecommerce.domain.member.repository;

import com.ecommerce.domain.member.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByMemberMemberId(String memberId);

    Address findByMemberMemberIdAndAddressId(String memberId, Long addressId);
}
