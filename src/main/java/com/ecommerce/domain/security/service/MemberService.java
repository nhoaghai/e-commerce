package com.ecommerce.domain.security.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.security.dto.request.AccountRequest;
import com.ecommerce.domain.security.dto.request.ChangeAddressRequest;
import com.ecommerce.domain.security.dto.request.ChangeAvatarRequest;
import com.ecommerce.domain.security.dto.request.ChangePasswordRequest;
import com.ecommerce.domain.security.dto.response.AccountResponse;
import com.ecommerce.domain.security.dto.response.AddressResponse;
import com.ecommerce.domain.security.dto.response.MemberInfoResponse;
import com.ecommerce.domain.security.model.Member;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface MemberService {
    AccountResponse findById();

    Member findByEmail(String email);

    AccountResponse updateProfile(AccountRequest accountRequest);

    MessageResponse changeAvatar(ChangeAvatarRequest changeAvatarRequest);

    List<AddressResponse> changeAddress(ChangeAddressRequest changeAddressRequest, Long addressId);

    Boolean existByEmail(String email);

    MessageResponse changePassword(ChangePasswordRequest changePasswordRequest);

    Member getUserDetailsFromRefreshToken(String token) throws Exception;

    Member getUserDetailFromToken(String token) throws Exception;
}
