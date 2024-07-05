package com.ecommerce.domain.member.serviceImpl;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.member.dto.request.SellerSignUpRequest;
import com.ecommerce.domain.member.exception.SellerException;
import com.ecommerce.domain.member.model.Seller;
import com.ecommerce.domain.member.repository.SellerRepository;
import com.ecommerce.domain.member.service.SellerService;
import com.ecommerce.domain.product.dto.request.ProductRequest;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import com.ecommerce.domain.product.exception.ProductException;
import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.product.repository.CategoryRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.Role;
import com.ecommerce.domain.security.model.RoleName;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.serviceImpl.RoleServiceImpl;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RoleServiceImpl roleService;
    private final ModelMapper modelMapper;


    @Override
    public MessageResponse sellerSignUp(SellerSignUpRequest sellerSignUpRequest) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId());
        Role roleSeller = roleService.findByRoleName(RoleName.ROLE_SELLER);
        if (member.getRoles().contains(roleSeller)){
            throw SellerException.badRequest("Member already is seller");
        }
        member.getRoles().add(roleSeller);
        memberRepository.save(member);

        Seller seller = new Seller();
        seller.setShopName(sellerSignUpRequest.getShopName());
        seller.setEmail(sellerSignUpRequest.getEmail());
        seller.setAddressPickUp(sellerSignUpRequest.getAddressPickup());
        seller.setPhoneNumber(sellerSignUpRequest.getPhoneNumber());
        seller.setActive(true);
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
                    .message("Hello " + seller.getShopName())
                    .build();
        }
    }

    @Override
    public PageResponseDto<ProductResponse> findAllSellingProduct(Pageable pageable) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Product> page = productRepository.findAllBySellerSellerId(userDetails.getId(), pageable);

        if (page.isEmpty()){
            throw ProductException.notFound("No products is being sold!");
        } else {
            List<ProductResponse> data = page.stream()
                    .map(order -> modelMapper.map(order, ProductResponse.class))
                    .toList();
            return PageResponseDto.<ProductResponse>builder()
                    .data(data)
                    .totalPage(page.getTotalPages())
                    .pageNumber(page.getNumber())
                    .size(page.getSize())
                    .sort(page.getSort().toString())
                    .build();
        }
    }

    @Override
    public ProductResponse findSellingProduct(String sku) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product product = productRepository.findByProductSku(sku);
        if(!Objects.equals(userDetails.getId(), product.getSeller().getSellerId())) {
            throw new DomainException("No product of this sku found");
        }
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public ProductResponse addProduct(ProductRequest request) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Product> productList = productRepository.findAllBySellerSellerId(userDetails.getId());
        for(Product product:productList) {
            if (Objects.equals(product.getProductName(), request.getProductName())) {
                throw new DomainException("You already have a product of this name. Please choose another product name");
            }
        }

        Product product = modelMapper.map(request, Product.class);
        product.setActive(true);
        product.setSku(generateRandomSKU());
        product.setSeller(sellerRepository.findByMemberMemberId(userDetails.getId()));
        product.setCreateAt(LocalDateTime.now());
        product.setCategory(categoryRepository.findByCategoryName(request.getCategoryName()));

        productRepository.save(product);

        return modelMapper.map(product, ProductResponse.class);
    }

    public static String generateRandomSKU() {
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();

        String part1 = uuidStr.substring(0, 8);
        String part2 = uuidStr.substring(9, 13);
        String part3 = uuidStr.substring(14, 18);

        return part1 + "-" + part2 + "-" + part3;
    }



}
