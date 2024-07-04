package com.ecommerce.domain.shoppingCart.dto.mapper;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import com.ecommerce.domain.shoppingCart.dto.request.CartRequest;
import com.ecommerce.domain.shoppingCart.dto.response.CartResponse;
import com.ecommerce.domain.shoppingCart.model.ShoppingCart;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CartMapper {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public CartResponse cartMappingToResponse(ShoppingCart shoppingCart) {
        CartResponse response = new CartResponse();
        response.setProductQuantity(shoppingCart.getProductQuantity());
        response.setProductName(shoppingCart.getProduct().getProductName());
        response.setProductId(shoppingCart.getProduct().getProductId());
        response.setCartId(shoppingCart.getShoppingCartId());
        return response;
    }

    public ShoppingCart cartRequestMappingToCart(CartRequest request) throws DomainException {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ShoppingCart cart = new ShoppingCart();
        Product product = productRepository.findById(request.getProductId()).orElse(null);
        Member member = memberRepository.findByMemberId(userDetails.getId());
        cart.setProductQuantity(request.getProductQuantity());
        cart.setProduct(product);
        cart.setMember(member);
        return cart;
    }

}
