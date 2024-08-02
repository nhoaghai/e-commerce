package com.ecommerce.domain.security.serviceImpl;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.common.util.UploadService;
import com.ecommerce.domain.member.exception.SellerException;
import com.ecommerce.domain.member.model.Address;
import com.ecommerce.domain.member.model.Seller;
import com.ecommerce.domain.member.repository.AddressRepository;
import com.ecommerce.domain.member.repository.SellerRepository;
import com.ecommerce.domain.security.dto.request.AccountRequest;
import com.ecommerce.domain.security.dto.request.ChangeAvatarRequest;
import com.ecommerce.domain.security.dto.request.ChangePasswordRequest;
import com.ecommerce.domain.security.dto.response.AccountResponse;
import com.ecommerce.domain.security.exception.MemberException;
import com.ecommerce.domain.security.exception.TokenException;
import com.ecommerce.domain.security.jwt.JwtProvider;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.Role;
import com.ecommerce.domain.security.model.RoleName;
import com.ecommerce.domain.security.model.Token;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.repository.TokenRepository;
import com.ecommerce.domain.security.service.MemberService;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final SellerRepository sellerRepository;
    private final RoleServiceImpl roleService;
    private final ModelMapper modelMapper;
    private final UploadService uploadService;

    @Override
    public PageResponseDto<AccountResponse> findAllMember(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);

        List<AccountResponse> data = page.getContent().stream()
                .map(member -> {
                    AccountResponse response = modelMapper.map(member, AccountResponse.class);
                    response.setAddress(addressRepository.findByMemberMemberId(member.getMemberId()).stream()
                            .map(Address::getFullAddress).toList());
                    response.setRoles(memberRepository.findByMemberId(member.getMemberId()).orElseThrow(()-> new MemberException("Member not found!")).getRoles());
                    return response;
                })
                .toList();
        return PageResponseDto.<AccountResponse>builder()
                .data(data)
                .size(page.getSize())
                .totalPage(page.getTotalPages())
                .pageNumber(page.getNumber())
                .sort(page.getSort().toString())
                .build();
    }

    @Override
    public AccountResponse getMemberInfo() {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId()).orElseThrow(()-> new MemberException("Member not found!"));
        AccountResponse response = modelMapper.map(member, AccountResponse.class);
        response.setAddress(addressRepository.findByMemberMemberId(member.getMemberId()).stream()
                .map(Address::getFullAddress).toList());
        response.setRoles(memberRepository.findByMemberId(member.getMemberId()).orElseThrow(()-> new MemberException("Member not found!")).getRoles());
        return response;
    }

    @Override
    public Member findById(String memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> MemberException.notFound("could not found member with Id!"));
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> MemberException.notFound(email));
    }

    @Override
    public PageResponseDto<AccountResponse> findByRoleName(RoleName roleName, Pageable pageable) {
        Page<Member> page = memberRepository.findByRoleName(roleName, pageable);

        List<AccountResponse> data = page.getContent().stream()
                .map(member -> {
                    AccountResponse response = modelMapper.map(member, AccountResponse.class);
                    response.setAddress(addressRepository.findByMemberMemberId(member.getMemberId()).stream()
                            .map(Address::getFullAddress).toList());
                    response.setRoles(memberRepository.findByMemberId(member.getMemberId()).orElseThrow(()-> new MemberException("Member not found!")).getRoles());
                    return response;
                })
                .toList();
        return PageResponseDto.<AccountResponse>builder()
                .data(data)
                .size(page.getSize())
                .totalPage(page.getTotalPages())
                .pageNumber(page.getNumber())
                .sort(page.getSort().toString())
                .build();
    }

    @Override
    public AccountResponse addNewRoleToMember(String memberId, RoleName roleName) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(()-> new MemberException("Member not found!"));
        Role role = roleService.findByRoleName(roleName);
        if (member.getRoles().contains(role)) {
            throw MemberException.conflict("Member " + member.getFullName() + " already had this role");
        }
        member.getRoles().add(role);
        memberRepository.save(member);
        return modelMapper.map(member, AccountResponse.class);
    }

    @Override
    public AccountResponse deleteRoleOfMember(String memberId, RoleName roleName) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(()-> new MemberException("Member not found!"));
        Role role = roleService.findByRoleName(roleName);
        if (!member.getRoles().contains(role)) {
            throw MemberException.notFound("Member " + member.getFullName() + " does not have this role");
        }
        member.getRoles().remove(role);
        memberRepository.save(member);
        return modelMapper.map(member, AccountResponse.class);
    }

    @Override
    public MessageResponse toggleMemberStatusById(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(()-> new MemberException("Member not found!"));
        member.setActive(!member.isActive());
        memberRepository.save(member);
        return MessageResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Account member status changed successfully!")
                .build();
    }

    @Override
    public MessageResponse toggleSellerStatusById(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(()-> new MemberException("Member not found!"));
        Role roleSeller = roleService.findByRoleName(RoleName.ROLE_SELLER);
        if (!member.getRoles().contains(roleSeller)){
            throw MemberException.notFound("Member " + member.getFullName() + " does not have this role");
        }
        Seller seller = sellerRepository.findByMemberMemberId(memberId)
                .orElseThrow(()-> new SellerException("Seller not found!"));
        seller.setActive(!seller.isActive());
        sellerRepository.save(seller);
        return MessageResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Account seller status changed successfully!")
                .build();
    }

    @Override
    public Boolean existByEmail(String email) {
        return null;
    }

    @Override
    public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId()).orElseThrow(()-> new MemberException("Member not found!"));

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
        Member member = memberRepository.findByMemberId(memberDetail.getId()).orElseThrow(()-> new MemberException("Member not found!"));

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
        Member member = memberRepository.findByMemberId(memberDetail.getId()).orElseThrow(()-> new MemberException("Member not found!"));
        member.setAvatarUrl(uploadService.uploadFile(changeAvatarRequest.getAvatarUrl()));
        member.setUpdateAt(LocalDateTime.now());
        memberRepository.save(member);
        return MessageResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Change avatar successfully!")
                .build();
    }

    @Override
    public Member getUserDetailsFromRefreshToken(String token) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(token).orElseThrow(()-> new TokenException("Token not found!"));
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
