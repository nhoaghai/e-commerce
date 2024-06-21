package com.ecommerce.domain.security.serviceImpl;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.security.dto.request.LoginRequest;
import com.ecommerce.domain.security.dto.request.RefreshTokenDto;
import com.ecommerce.domain.security.dto.request.SignUpRequest;
import com.ecommerce.domain.security.dto.response.MemberInfoResponse;
import com.ecommerce.domain.security.exception.LoginFailException;
import com.ecommerce.domain.security.jwt.JwtProvider;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.Role;
import com.ecommerce.domain.security.model.RoleName;
import com.ecommerce.domain.security.model.Token;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.service.AuthenticationService;
import com.ecommerce.domain.security.serviceImpl.jwtService.TokenServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtProvider jwtProvider;
    private final RoleServiceImpl roleService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final MemberServiceImpl memberService;
    private final TokenServiceImpl tokenService;

    private boolean isMobileDevice(String userAgent){
        //user-agent get from header
        return userAgent.toLowerCase().contains("mobile");
    }
    @Override
    public MessageResponse handleRegisterMember(SignUpRequest signUpRequest) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByRoleName(RoleName.ROLE_MEMBER));

        Member member = new Member();
        member.setFirstName(signUpRequest.getFirstName());
        member.setLastName(signUpRequest.getLastName());
        member.setEmail(signUpRequest.getEmail());
        member.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        member.setCreateAt(LocalDateTime.now());
        member.setUpdateAt(LocalDateTime.now());
        member.setActive(true);
        member.setRoles(roles);
        member.setPhoneNumber(signUpRequest.getPhoneNumber());
        member.setBirthday(signUpRequest.getBirthDate());

        memberRepository.save(member);

        return MessageResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("Member register successfully!")
                .build();
    }

    @Override
    public MemberInfoResponse handleAuthenticateMember(LoginRequest loginRequest, HttpServletRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            throw new LoginFailException("Email or password incorrect!");
        }

        Member member = memberService.findByEmail(loginRequest.getEmail());
        String accessToken = jwtProvider.generateTokenFromEmail(loginRequest.getEmail());
        String userAgent = request.getHeader("User-agent");
        String email = jwtProvider.getEmailFromJwtToken(accessToken);
        Member memberDetail = memberService.findByEmail(email);
        Token jwtToken = tokenService.addToken(memberDetail,accessToken,isMobileDevice(userAgent));

        List<String> roles = member.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .toList();

        return MemberInfoResponse.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .fullName(member.getFullName())
                .accessToken(accessToken)
                .refreshToken(jwtToken.getRefreshToken())
                .expiryDate(jwtToken.getExpirationDate())
                .roles(roles)
                .avatarUrl(member.getAvatarUrl())
                .tokenType("Bearer ")
                .build();
    }

    @Override
    public MemberInfoResponse handleRefreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        Member member = memberService.getUserDetailsFromRefreshToken(refreshTokenDto.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(refreshTokenDto.getRefreshToken(),member);

        List<String> roles = member.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .toList();

        return MemberInfoResponse.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .fullName(member.getFullName())
                .accessToken(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .expiryDate(jwtToken.getExpirationDate())
                .roles(roles)
                .avatarUrl(member.getAvatarUrl())
                .tokenType("Bearer ")
                .build();
    }
}
