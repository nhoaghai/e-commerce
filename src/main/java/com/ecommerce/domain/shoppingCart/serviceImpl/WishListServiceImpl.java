package com.ecommerce.domain.shoppingCart.serviceImpl;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import com.ecommerce.domain.shoppingCart.dto.mapper.WishListMapper;
import com.ecommerce.domain.shoppingCart.dto.request.WishListRequest;
import com.ecommerce.domain.shoppingCart.dto.response.WishListResponse;
import com.ecommerce.domain.shoppingCart.exception.WishListException;
import com.ecommerce.domain.shoppingCart.model.WishList;
import com.ecommerce.domain.shoppingCart.repository.WishListRepository;
import com.ecommerce.domain.shoppingCart.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {
    private final WishListRepository wishListRepository;
    private final WishListMapper wishListMapper;

    @Override
    public PageResponseDto<WishListResponse> getAllWistlistItems(Pageable pageable) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<WishList> wishPage = wishListRepository.findAllByMemberMemberId(userDetails.getId(), pageable);

        if (wishPage.isEmpty()) {
            throw WishListException.notFound("You have no item in your wish list!");
        } else {
            List<WishListResponse> wlData = wishPage.getContent().stream()
                    .map(wishListMapper::wishListMappingToResponse)
                    .toList();
            PageResponseDto<WishListResponse> pageResponseDto = new PageResponseDto<>();
            pageResponseDto.setData(wlData);
            pageResponseDto.setTotalPage(wishPage.getTotalPages());
            pageResponseDto.setSize(wishPage.getSize());
            pageResponseDto.setPageNumber(wishPage.getNumber());
            pageResponseDto.setSort(wishPage.getSort().toString());

            return pageResponseDto;
        }
    }

    @Override
    public WishListResponse addProductToWishList(WishListRequest request) {
        checkValidUser(request.getMemberId());
        WishList wishList = wishListRepository.findFirstByProductProductId(request.getProductId()).orElseThrow(()-> new WishListException("WishList not found!"));
        // Case 1: wishList doesn't exist yet
        // --> Create a new wish list out of the req and add the product to it
        if (wishList == null) {
            wishList = wishListMapper.wishListRequestMappingToWishList(request);
            errorHandler(wishList, request);
            wishListRepository.save(wishList);
        } else {
        // Case 2: wishList already exists and contains the desired product
        // --> just save the wish list
            errorHandler(wishList, request);
            wishListRepository.save(wishList);
        }
        return wishListMapper.wishListMappingToResponse(wishList);
    }

    @Override
    public MessageResponse removeProductFromWishListById(Integer wishListId) {
        WishList wishList = wishListRepository.findById(wishListId).orElseThrow();
        checkValidUser(wishList.getMember().getMemberId());
        wishListRepository.delete(wishList);
        return MessageResponse.builder()
                .message("Deleted product from your wish list successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public MessageResponse clearWishList() {
        wishListRepository.deleteAll();
        return MessageResponse.builder()
                .message("Clear all items in wish list successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public void errorHandler(WishList wlist, WishListRequest request) {
        if(wlist.getProduct() == null) {
            throw WishListException.notFound("Product is null");
        } else if (wlist.getMember() == null) {
            throw WishListException.notFound("Member is null");
        }
    }

    public void checkValidUser(String memberId) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!memberId.equals(userDetails.getId())) {
            throw new DomainException("Incompatible member ID, please retry");
        }
    }

}
