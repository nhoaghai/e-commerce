package com.ecommerce.controller.member;

import com.ecommerce.domain.security.dto.response.MemberInfoResponse;
import com.ecommerce.domain.security.serviceImpl.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member/account")
public class AccountController {
    private final MemberServiceImpl memberService;

    @GetMapping
    public ResponseEntity<MemberInfoResponse> getMemberInfo(){
        return ResponseEntity.ok(memberService.findById());
    }
}
