package com.ecommerce.domain.security.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.security.dto.request.AccountRequest;
import com.ecommerce.domain.member.dto.request.AddressRequest;
import com.ecommerce.domain.security.dto.request.ChangeAvatarRequest;
import com.ecommerce.domain.security.dto.request.ChangePasswordRequest;
import com.ecommerce.domain.security.dto.response.AccountResponse;
import com.ecommerce.domain.member.dto.response.AddressResponse;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberService {

    PageResponseDto<AccountResponse> findAllMember(Pageable pageable);

    AccountResponse getMemberInfo();

    Member findById(String memberId);

    Member findByEmail(String email);

    PageResponseDto<AccountResponse> findByRoleName(RoleName roleName, Pageable pageable);

    AccountResponse addNewRoleToMember(String memberId, RoleName roleName);

    AccountResponse deleteRoleOfMember(String memberId, RoleName roleName);

    AccountResponse updateProfile(AccountRequest accountRequest);

    MessageResponse changeAvatar(MultipartFile changeAvatarRequest);

    MessageResponse toggleMemberStatusById(String memberId);

    MessageResponse toggleSellerStatusById(String memberId);

    Boolean existByEmail(String email);

    MessageResponse changePassword(ChangePasswordRequest changePasswordRequest);

    Member getUserDetailsFromRefreshToken(String token) throws Exception;

    Member getUserDetailFromToken(String token) throws Exception;
}
