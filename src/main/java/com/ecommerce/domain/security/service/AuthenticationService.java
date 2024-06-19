package com.ecommerce.domain.security.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.security.dto.request.LoginRequest;
import com.ecommerce.domain.security.dto.request.RefreshTokenDto;
import com.ecommerce.domain.security.dto.request.SignUpRequest;
import com.ecommerce.domain.security.dto.response.MemberInfoResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    MessageResponse handleRegisterMember(SignUpRequest signUpRequest);

    MemberInfoResponse handleAuthenticateMember(LoginRequest loginRequest, HttpServletRequest request) throws Exception;

    MemberInfoResponse handleRefreshToken (RefreshTokenDto refreshTokenDto) throws Exception;
}
