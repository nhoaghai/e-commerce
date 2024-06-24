package com.ecommerce.domain.shoppingCart.serviceImpl;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import com.ecommerce.domain.shoppingCart.dto.mapper.CartMapper;
import com.ecommerce.domain.shoppingCart.dto.request.CartRequest;
import com.ecommerce.domain.shoppingCart.dto.response.CartResponse;
import com.ecommerce.domain.shoppingCart.exception.CartException;
import com.ecommerce.domain.shoppingCart.model.ShoppingCart;
import com.ecommerce.domain.shoppingCart.repository.CartRepository;
import com.ecommerce.domain.shoppingCart.service.CartService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Override
    public List<CartResponse> findAllCart() {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ShoppingCart> carts = cartRepository.findAllByMemberMemberId(userDetails.getId());

        if (carts.isEmpty()){
            throw CartException.notFound("No products in your cart!");
        }else {
            return carts.stream()
                    .map(cartMapper::cartMappingToResponse
                    )
                    .toList();
        }
    }

    @Override
    public CartResponse addNewProductIntoCart(CartRequest cartRequest) {
        ShoppingCart cart = cartRepository.findFirstByProductProductId(cartRequest.getProductId());
        if(cart == null) {
            cart = cartMapper.cartRequestMappingToCart(cartRequest);
            errorHandler(cart, cartRequest);
            cartRepository.save(cart);
        } else {
            errorHandler(cart, cartRequest);
            // Add product quantity if product is already in cart
            cart.setProductQuantity(cart.getProductQuantity() + cartRequest.getProductQuantity());
            if(cart.getProductQuantity() > 0) {
                cartRepository.save(cart);
            } else {
                deleteProductInCart(cartRequest);
            }
        }
        return cartMapper.cartMappingToResponse(cart);
    }

    @Override
    public CartResponse changeProductNumberInCart(CartRequest cartRequest) {
        ShoppingCart cart = cartRepository.findFirstByProductProductId(cartRequest.getProductId());
        if(cart != null) {
            errorHandler(cart, cartRequest);
            // Add product quantity if product is already in cart
            cart.setProductQuantity(cart.getProductQuantity() + cartRequest.getProductQuantity());
            if(cart.getProductQuantity() > 0) {
                cartRepository.save(cart);
            } else {
                deleteProductInCart(cartRequest);
            }
        } else {
            throw CartException.notFound("Product not found in your cart");
        }
        return cartMapper.cartMappingToResponse(cart);
    }

    @Override
    public MessageResponse deleteProductInCart(CartRequest cartRequest) {
        ShoppingCart cart = cartRepository.findFirstByProductProductId(cartRequest.getProductId());
        if(cart == null) {
            throw CartException.notFound("No instance of this product in your cart");
        } else {
            cartRepository.delete(cart);
        }
        return MessageResponse.builder()
                .message("Delete product in cart successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public MessageResponse clearAllProductsInCart() {
        cartRepository.deleteAll();
        return MessageResponse.builder()
                .message("Clear all products in cart successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public void errorHandler(ShoppingCart cart, CartRequest cartRequest) {
        if(cart.getProduct() == null || cart.getMember() == null) {
            throw CartException.notFound("Product or Member is null");
        }
        if(cart.getProductQuantity() <= 0) {
            throw CartException.badRequest("Quantity must be a positive integer");
        }
    }

}
