package com.ecommerce.domain.member.serviceImpl;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.member.dto.request.AddressRequest;
import com.ecommerce.domain.member.dto.response.AddressResponse;
import com.ecommerce.domain.member.exception.AddressException;
import com.ecommerce.domain.member.model.Address;
import com.ecommerce.domain.member.repository.AddressRepository;
import com.ecommerce.domain.member.service.AddressService;
import com.ecommerce.domain.security.exception.MemberException;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.repository.MemberRepository;
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

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponseDto<AddressResponse> getAllAddressList(Pageable pageable) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Address> page = addressRepository.findAllByMemberMemberId(memberDetail.getId(), pageable);
        List<AddressResponse> data = page.stream()
                .map(address -> modelMapper.map(address, AddressResponse.class))
                .toList();

        return PageResponseDto.<AddressResponse>builder()
                .data(data)
                .pageNumber(page.getNumber())
                .totalPage(page.getTotalPages())
                .size(page.getSize())
                .sort(page.getSort().toString())
                .build();
    }

    @Override
    public MessageResponse addNewAddress(AddressRequest addressRequest) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Address address = new Address();
        address.setFullAddress(addressRequest.getFullAddress());
        address.setReceiveName(addressRequest.getReceiveName());
        address.setPhoneNumber(addressRequest.getPhoneNumber());
        address.setMember(memberRepository.findByMemberId(memberDetail.getId()).orElseThrow(()-> new MemberException("Member not found!")));
        addressRepository.save(address);
        return MessageResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Add new address successfully!")
                .build();
    }

    @Override
    public List<AddressResponse> changeAddress(AddressRequest addressRequest, Long addressId) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId()).orElseThrow(()-> new MemberException("Member not found!")); // optional
        Address address = addressRepository.findByMemberMemberIdAndAddressId(memberDetail.getId(), addressId)
                .orElseThrow(()-> new AddressException("Address not found!"));
        if (address == null) {
            throw new AddressException("No product found matching the search criteria");
        } else {
            address.setFullAddress(addressRequest.getFullAddress());
            address.setPhoneNumber(addressRequest.getPhoneNumber());
            address.setReceiveName(addressRequest.getReceiveName());
            addressRepository.save(address);
            member.setUpdateAt(LocalDateTime.now());
            return addressRepository.findByMemberMemberId(memberDetail.getId()).stream()
                    .map(add -> modelMapper.map(add, AddressResponse.class))
                    .toList();
        }
    }

    @Override
    public AddressResponse findAddressById(Long addressId) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Address address = addressRepository.findByMemberMemberIdAndAddressId(memberDetail.getId(), addressId)
                .orElseThrow(()-> new AddressException("Address not found!"));
        if (address == null) {
            throw new AddressException("No product found matching the search criteria");
        } else {
            return modelMapper.map(address, AddressResponse.class);
        }
    }

    @Override
    public MessageResponse deleteAddressById(Long addressId) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Address address = addressRepository.findByMemberMemberIdAndAddressId(memberDetail.getId(), addressId)
                .orElseThrow(()-> new AddressException("Address not found!"));
        if (address == null) {
            throw new AddressException("No product found matching the search criteria");
        } else {
            addressRepository.delete(address);
            return MessageResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Delete address successfully!")
                    .build();
        }
    }
}
