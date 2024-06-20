package com.ecommerce.domain.security.repository;

import com.ecommerce.domain.security.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);

    Member findByMemberId(String memberId);

    boolean existsByEmail(String email);
}
