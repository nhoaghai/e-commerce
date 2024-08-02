package com.ecommerce.domain.security.repository;

import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberId(String memberId);

    boolean existsByEmail(String email);

//    @Query(value = "select m from Member m where m.roles in (select r from Role r where r.roleName = :roleName)")
//    Member findByRoleName(String roleName);

    @Query(value = "select distinct m from Member m join m.roles r where r.roleName = :roleName")
    Page<Member> findByRoleName(@Param("roleName") RoleName roleName, Pageable pageable);

//    @Query(value = "select distinct m.roles. from Member m join m.roles r where r.roleName = :roleName")
}
