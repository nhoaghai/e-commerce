package com.ecommerce.controller.permitAll;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.security.dto.request.LoginRequest;
import com.ecommerce.domain.security.dto.request.SignUpRequest;
import com.ecommerce.domain.security.dto.response.MemberInfoResponse;
import com.ecommerce.domain.security.serviceImpl.AuthenticationServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v3/auth")
public class AuthController {

    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponse> registerMember(@Valid @RequestBody SignUpRequest signUpRequest){
        return ResponseEntity.ok(authenticationService.handleRegisterMember(signUpRequest));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<MemberInfoResponse> authenticateMember(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest) throws Exception {
        return ResponseEntity.ok(authenticationService.handleAuthenticateMember(loginRequest,httpServletRequest));
    }
}
