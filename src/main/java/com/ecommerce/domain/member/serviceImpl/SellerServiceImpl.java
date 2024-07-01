package com.ecommerce.domain.member.serviceImpl;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.member.dto.request.SellerSignUpRequest;
import com.ecommerce.domain.member.exception.SellerException;
import com.ecommerce.domain.member.model.Seller;
import com.ecommerce.domain.member.repository.SellerRepository;
import com.ecommerce.domain.member.service.SellerService;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.RoleName;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.serviceImpl.RoleServiceImpl;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final RoleServiceImpl roleService;
    @Override
    public MessageResponse sellerSignUp(SellerSignUpRequest sellerSignUpRequest) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId());
        member.getRoles().add(roleService.findByRoleName(RoleName.ROLE_SELLER));

        Seller seller = new Seller();
        seller.setShopName(sellerSignUpRequest.getShopName());
        seller.setEmail(sellerSignUpRequest.getEmail());
        seller.setPhoneNumber(sellerSignUpRequest.getPhoneNumber());
        seller.setMember(member);
        sellerRepository.save(seller);
        return MessageResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("Register to seller successfully!")
                .build();
    }

    @Override
    public MessageResponse sellerSignIn() {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId());
        if (!member.getRoles().contains(roleService.findByRoleName(RoleName.ROLE_SELLER))){
            throw new SellerException("Member did not register to be a seller!");
        }

        Seller seller = sellerRepository.findByMemberMemberId(memberDetail.getId());

        if (!seller.isActive()) {
            throw new SellerException("Member was banned! Contact to admin to unlock!");
        } else {
            return MessageResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Hello "+seller.getShopName())
                    .build();
        }
    }
}
