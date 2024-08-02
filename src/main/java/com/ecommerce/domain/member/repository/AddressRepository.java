package com.ecommerce.domain.member.repository;

import com.ecommerce.domain.member.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByMemberMemberId(String memberId);

    Optional<Address> findByMemberMemberIdAndAddressId(String memberId, Long addressId);

    @Query(value = "select a from Address a where a.member.memberId =?1")
    Page<Address> findAllByMemberMemberId(String memberId, Pageable pageable);
}
