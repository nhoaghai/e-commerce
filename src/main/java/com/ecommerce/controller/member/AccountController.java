package com.ecommerce.controller.member;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.member.serviceImpl.AddressServiceImpl;
import com.ecommerce.domain.security.dto.request.AccountRequest;
import com.ecommerce.domain.member.dto.request.AddressRequest;
import com.ecommerce.domain.security.dto.request.ChangeAvatarRequest;
import com.ecommerce.domain.security.dto.request.ChangePasswordRequest;
import com.ecommerce.domain.security.dto.response.AccountResponse;
import com.ecommerce.domain.member.dto.response.AddressResponse;
import com.ecommerce.domain.security.serviceImpl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member/account")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {
    private final MemberServiceImpl memberService;
    private final AddressServiceImpl addressService;

    @GetMapping("/profile")
    public ResponseEntity<AccountResponse> getMemberInfo() {
        return ResponseEntity.ok(memberService.getMemberInfo());
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
    public ResponseEntity<List<AddressResponse>> updateAddress(@RequestBody AddressRequest addressRequest, @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.changeAddress(addressRequest, addressId));
    }

    @PutMapping("/changePassword")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        return ResponseEntity.ok(memberService.changePassword(changePasswordRequest));
    }
}
