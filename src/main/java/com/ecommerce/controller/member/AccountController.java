package com.ecommerce.controller.member;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.security.dto.request.AccountRequest;
import com.ecommerce.domain.security.dto.request.ChangeAddressRequest;
import com.ecommerce.domain.security.dto.request.ChangeAvatarRequest;
import com.ecommerce.domain.security.dto.request.ChangePasswordRequest;
import com.ecommerce.domain.security.dto.response.AccountResponse;
import com.ecommerce.domain.security.dto.response.AddressResponse;
import com.ecommerce.domain.security.dto.response.MemberInfoResponse;
import com.ecommerce.domain.security.serviceImpl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member/account")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {
    private final MemberServiceImpl memberService;


    @GetMapping("/profile")
    public ResponseEntity<AccountResponse> getMemberInfo() {
        return ResponseEntity.ok(memberService.findById());
    }

    @PutMapping("/profile")
    public ResponseEntity<AccountResponse> updateProfile(@RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(memberService.updateProfile(accountRequest));
    }

    @PutMapping("/changeAvatar")
    public ResponseEntity<MessageResponse> updateAvatar(@ModelAttribute ChangeAvatarRequest changeAvatarRequest) {
        return ResponseEntity.ok(memberService.changeAvatar(changeAvatarRequest));
    }

    @PutMapping("/changeAddress/{addressId}")
    public ResponseEntity<List<AddressResponse>> updateAddress(@RequestBody ChangeAddressRequest changeAddressRequest, @PathVariable Long addressId) {
        return ResponseEntity.ok(memberService.changeAddress(changeAddressRequest, addressId));
    }

    @PutMapping("/changePassword")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        return ResponseEntity.ok(memberService.changePassword(changePasswordRequest));
    }
}
