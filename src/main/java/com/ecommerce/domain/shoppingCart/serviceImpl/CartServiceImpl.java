package com.ecommerce.domain.shoppingCart.serviceImpl;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import com.ecommerce.domain.shoppingCart.dto.mapper.CartMapper;
import com.ecommerce.domain.shoppingCart.dto.request.CartRequest;
import com.ecommerce.domain.shoppingCart.dto.response.CartResponse;
import com.ecommerce.domain.shoppingCart.exception.CartException;
import com.ecommerce.domain.shoppingCart.model.ShoppingCart;
import com.ecommerce.domain.shoppingCart.repository.CartRepository;
import com.ecommerce.domain.shoppingCart.service.CartService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;


    @Override
    public PageResponseDto<CartResponse> findAllCart(Pageable pageable) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<ShoppingCart> carts = cartRepository.findAllByMemberMemberId(userDetails.getId(), pageable);

        if (carts.isEmpty()){
            throw CartException.notFound("No products in your cart!");
        } else{
            PageResponseDto<CartResponse> pageRDTO = new PageResponseDto<>();
            List<CartResponse> cartData = carts.getContent().stream()
                    .map(cartMapper::cartMappingToResponse
                    )
                    .toList();
            pageRDTO.setData(cartData);
            pageRDTO.setPageNumber(carts.getNumber());
            pageRDTO.setTotalPage(carts.getTotalPages());
            pageRDTO.setSize(carts.getSize());
            pageRDTO.setSort(carts.getSort().toString());
            return pageRDTO;
        }
    }

    @Override
    public CartResponse addNewProductIntoCart(CartRequest cartRequest) {
        checkValidUser(cartRequest.getMemberId());
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
                deleteProductInCart(cart.getShoppingCartId());
            }
        }
        return cartMapper.cartMappingToResponse(cart);
    }

    @Override
    public CartResponse changeProductQuantityInCart(CartRequest cartRequest) {
        checkValidUser(cartRequest.getMemberId());
        ShoppingCart cart = cartRepository.findFirstByProductProductId(cartRequest.getProductId());
        if(cart != null) {
            errorHandler(cart, cartRequest);
            // Add product quantity if product is already in cart
            cart.setProductQuantity(cart.getProductQuantity() + cartRequest.getProductQuantity());
            if(cart.getProductQuantity() > 0) {
                cartRepository.save(cart);
            } else {
                deleteProductInCart(cart.getShoppingCartId());
            }
        } else {
            throw CartException.notFound("Product not found in your cart");
        }
        return cartMapper.cartMappingToResponse(cart);
    }

    @Override
    public MessageResponse deleteProductInCart(Integer cartId) {
        ShoppingCart cart = cartRepository.findById(Long.valueOf(cartId)).orElseThrow();
        checkValidUser(cart.getMember().getMemberId());
        cartRepository.delete(cart);
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
    public void checkValidUser(String memberId) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!Objects.equals(memberId, userDetails.getId())) {
            throw new DomainException("Cannot find this order in your account, please retry");
        }
    }


}
