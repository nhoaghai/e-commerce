package com.ecommerce.domain.shoppingCart.dto.mapper;

import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import com.ecommerce.domain.shoppingCart.dto.request.WishListRequest;
import com.ecommerce.domain.shoppingCart.dto.response.WishListResponse;
import com.ecommerce.domain.shoppingCart.model.WishList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WishListMapper {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    // Out: Entity -> DTO
    public WishListResponse wishListMappingToResponse(WishList wishList) {
        WishListResponse response = new WishListResponse();
        response.setWishListId(wishList.getWishListId());
        response.setProductId(wishList.getProduct().getProductId());
        response.setProductName(wishList.getProduct().getProductName());
        return response;
    }

    // In: DTO -> Entity
    // throws DomainException
    public WishList wishListRequestMappingToWishList(WishListRequest request) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WishList wishList = new WishList();
        Product product = productRepository.findById(request.getProductId()).orElse(null);
        Member member = memberRepository.findByMemberId(userDetails.getId());
        wishList.setProduct(product);
        wishList.setMember(member);
        return wishList;
    }
}
