package com.ecommerce.controller.admin;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.security.dto.response.AccountResponse;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.RoleName;
import com.ecommerce.domain.security.serviceImpl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/admin/members")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminMemberController {
    private final MemberServiceImpl memberService;

    @GetMapping
    public ResponseEntity<PageResponseDto<AccountResponse>> getAllMember(@SortDefault(sort = "memberId") Pageable pageable) {
        return ResponseEntity.ok(memberService.findAllMember(pageable));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<Member> findMemberById(@PathVariable String memberId) {
        return ResponseEntity.ok(memberService.findById(memberId));
    }

    @GetMapping("/roleName")
    public ResponseEntity<PageResponseDto<AccountResponse>> findByRoleName(@RequestParam RoleName roleName, @SortDefault(sort = "memberId") Pageable pageable) {
        return ResponseEntity.ok(memberService.findByRoleName(roleName, pageable));
    }

    @PostMapping("/memberId/roles/roleName")
    public ResponseEntity<AccountResponse> addNewRoleToMember(@RequestParam(value = "memberId") String memberId, @RequestParam(value = "roleName") RoleName roleName) {
        return ResponseEntity.ok(memberService.addNewRoleToMember(memberId, roleName));
    }

    @DeleteMapping("/memberId/roles/roleName")
    public ResponseEntity<AccountResponse> DeleteRoleOfMember(@RequestParam(value = "memberId") String memberId, @RequestParam(value = "roleName") RoleName roleName) {
        return ResponseEntity.ok(memberService.deleteRoleOfMember(memberId, roleName));
    }

    @PatchMapping("/memberId")
    public ResponseEntity<MessageResponse> toggleMemberStatus(@RequestParam String memberId){
        return ResponseEntity.ok(memberService.toggleMemberStatusById(memberId));
    }

    @PatchMapping("/sellerId")
    public ResponseEntity<MessageResponse> toggleSellerStatus(@RequestParam String sellerId){
        return ResponseEntity.ok(memberService.toggleSellerStatusById(sellerId));
    }
}
