package com.ecommerce.domain.security.serviceImpl;

import com.ecommerce.domain.security.dto.request.ChangePasswordRequest;
import com.ecommerce.domain.security.dto.response.MemberInfoResponse;
import com.ecommerce.domain.security.exception.MemberException;
import com.ecommerce.domain.security.jwt.JwtProvider;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.Token;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.repository.TokenRepository;
import com.ecommerce.domain.security.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;
    private final ModelMapper modelMapper;

    @Override
    public MemberInfoResponse findById() {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId());
        Token memberToken = tokenRepository.findByMemberMemberId(memberDetail.getId());

        return MemberInfoResponse.builder()
                .id(member.getMemberId())
                .email(member.getMemberId())
                .phoneNumber(member.getPhoneNumber())
                .fullName(member.getFullName())
                .accessToken(memberToken.getTokenValue())
                .refreshToken(memberToken.getRefreshToken())
                .roles(member.getRoles().stream().map(role -> role.getRoleName().name()).toList())
                .expiryDate(memberToken.getExpirationDate())
                .avatarUrl(member.getAvatarUrl())
                .tokenType(memberToken.getTokenType())
                .build();
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> MemberException.notFound(email));
    }

    @Override
    public Boolean existByEmail(String email) {
        return null;
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {

    }

    @Override
    public Member getUserDetailsFromRefreshToken(String token) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(token);
        return getUserDetailFromToken(existingToken.getTokenType());
    }

    @Override
    public Member getUserDetailFromToken(String token) throws Exception {
        if (jwtProvider.isTokenExpired(token)) {
            throw new Exception("Token is expired!");
        }
        String subject = jwtProvider.getSubject(token);
        Optional<Member> member;
        member = memberRepository.findByEmail(subject);
        return member.orElseThrow(() -> new Exception("Member not found!"));
    }
}
