package com.ecommerce.domain.security.serviceImpl.jwtService;

import com.ecommerce.domain.security.jwt.JwtProvider;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.Token;
import com.ecommerce.domain.security.repository.TokenRepository;
import com.ecommerce.domain.security.service.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.expired.refresh-token}")
    private Long expirationRefreshToken;

    @Value("${jwt.expired.access-token}")
    private Long expiration;

    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    private static final int MAX_TOKEN = 3;

    @Override
    public Optional<Token> findByTokenValue(String tokenValue) {
        return tokenRepository.findByTokenValue(tokenValue);
    }

    @Override
    public Token addToken(Member member, String token, boolean isMobileDevice) throws Exception {
        List<Token> memberTokens = tokenRepository.findByMember(member);
        int tokenCount = memberTokens.size();
        //If token count more than number of max token, delete the oldest token
        if (tokenCount > MAX_TOKEN) {
            //Check the non-mobile token
            boolean hasMobileToken = memberTokens.stream().anyMatch(Token::isMobile);
            //true = has Mobile, false = don't have any mobile
            Token tokenDelete;
            if (hasMobileToken) {
                //token will be deleted is the first mobile token
                tokenDelete = memberTokens.stream()
                        .filter(Token::isMobile).findFirst().orElseThrow(() -> new Exception("Not found mobile Token"));
            } else {
                //delete the first token of other device
                tokenDelete = memberTokens.getFirst();
            }
            tokenRepository.delete(tokenDelete);
        }
        long expirationMs = expiration;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expirationMs);

        // create new token
        Token newToken = Token.builder()
                .member(member)
                .tokenValue(token)
                .revoke(false)
                .expired(false)
                .tokenType("Bearer ")
                .expirationDate(expirationDateTime)
                .isMobile(isMobileDevice)
                .build();
        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(newToken);
        return newToken;
    }

    @Transactional
    @Override
    public Token refreshToken(String refreshToken, Member member) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        if (existingToken == null){
            throw new Exception("Refresh token does not exist!");
        }
        if (existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())){
            tokenRepository.delete(existingToken);
            throw new Exception("Refresh token is expired!");
        }
        String token = jwtProvider.generateTokenFromEmail(member.getEmail());
        LocalDateTime exLocalDateTime = LocalDateTime.now().plusSeconds(expiration);
        existingToken.setExpirationDate(exLocalDateTime);
        existingToken.setTokenValue(token);
        existingToken.setRefreshToken(UUID.randomUUID().toString());
        existingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(existingToken);
        return existingToken;
    }
}
