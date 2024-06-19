package com.ecommerce.domain.security.service;

import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.Token;

import java.util.List;
import java.util.Optional;

public interface TokenService {
    Optional<Token> findByTokenValue(String tokenValue);

    Token addToken(Member member, String token, boolean isMobileDevice) throws Exception;

    Token refreshToken(String refreshToken, Member member) throws Exception;
}
