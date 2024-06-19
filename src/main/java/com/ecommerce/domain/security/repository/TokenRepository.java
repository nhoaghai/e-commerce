package com.ecommerce.domain.security.repository;

import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenValue(String tokenValue);

    List<Token> findByMember(Member member);

    Token findByRefreshToken(String refreshToken);
}
