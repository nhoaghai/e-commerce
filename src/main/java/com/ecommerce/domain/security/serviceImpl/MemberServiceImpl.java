package com.ecommerce.domain.security.serviceImpl;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.UploadService;
import com.ecommerce.domain.member.model.Address;
import com.ecommerce.domain.member.repository.AddressRepository;
import com.ecommerce.domain.security.dto.request.AccountRequest;
import com.ecommerce.domain.security.dto.request.ChangeAddressRequest;
import com.ecommerce.domain.security.dto.request.ChangeAvatarRequest;
import com.ecommerce.domain.security.dto.request.ChangePasswordRequest;
import com.ecommerce.domain.security.dto.response.AccountResponse;
import com.ecommerce.domain.security.dto.response.AddressResponse;
import com.ecommerce.domain.security.exception.MemberException;
import com.ecommerce.domain.security.jwt.JwtProvider;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.Token;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.repository.TokenRepository;
import com.ecommerce.domain.security.service.MemberService;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final UploadService uploadService;

    @Override
    public AccountResponse findById() {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId());
        AccountResponse response = modelMapper.map(member, AccountResponse.class);
        response.setAddress(addressRepository.findByMemberMemberId(member.getMemberId()).stream()
                .map(address -> address.getFullAddress()).toList());
        return response;
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
    public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId());

        boolean checkOldPassword = passwordEncoder.matches(changePasswordRequest.getOldPassword(), member.getPassword());
        if (checkOldPassword) {
            member.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            memberRepository.save(member);
        } else {
            throw new MemberException("Incorrect old password! Please try again!");
        }
        return MessageResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Change password successfully!")
                .build();
    }

    @Override
    public AccountResponse updateProfile(AccountRequest accountRequest) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId());

        if (accountRequest.getEmail() != null && !accountRequest.getEmail().isEmpty()) {
            member.setEmail(accountRequest.getEmail());
        }
        if (accountRequest.getFirstName() != null && !accountRequest.getFirstName().isEmpty()) {
            member.setFirstName(accountRequest.getFirstName());
        }
        if (accountRequest.getLastName() != null && !accountRequest.getLastName().isEmpty()) {
            member.setLastName(accountRequest.getLastName());
        }
        if (accountRequest.getPhoneNumber() != null && !accountRequest.getPhoneNumber().isEmpty()) {
            member.setPhoneNumber(accountRequest.getPhoneNumber());
        }
        if (accountRequest.isGender() != member.isGender()) {
            member.setGender(accountRequest.isGender());
        }
        if (accountRequest.getBirthday() != null) {
            member.setBirthday(accountRequest.getBirthday());
        }
        member.setUpdateAt(LocalDateTime.now());
        memberRepository.save(member);
        return modelMapper.map(member, AccountResponse.class);
    }

    @Override
    public MessageResponse changeAvatar(ChangeAvatarRequest changeAvatarRequest) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId());
        member.setAvatarUrl(uploadService.uploadFile(changeAvatarRequest.getAvatarUrl()));
        member.setUpdateAt(LocalDateTime.now());
        memberRepository.save(member);
        return MessageResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Change avatar successfully!")
                .build();
    }

    @Override
    public List<AddressResponse> changeAddress(ChangeAddressRequest changeAddressRequest, Long addressId) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId());
        Address address = addressRepository.findByMemberMemberIdAndAddressId(memberDetail.getId(), addressId);
        address.setFullAddress(changeAddressRequest.getFullAddress());
        address.setPhoneNumber(changeAddressRequest.getPhoneNumber());
        address.setReceiveName(changeAddressRequest.getReceiveName());
        addressRepository.save(address);
        member.setUpdateAt(LocalDateTime.now());
        return addressRepository.findByMemberMemberId(memberDetail.getId()).stream()
                .map(add -> modelMapper.map(add, AddressResponse.class))
                .toList();
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
