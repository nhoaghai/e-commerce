package com.ecommerce.domain.member.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.member.dto.request.AddressRequest;
import com.ecommerce.domain.member.dto.response.AddressResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AddressService {
    PageResponseDto<AddressResponse> getAllAddressList(Pageable pageable);

    MessageResponse addNewAddress(AddressRequest addressRequest);

    List<AddressResponse> changeAddress(AddressRequest addressRequest, Long addressId);

    AddressResponse findAddressById(Long addressId);

    MessageResponse deleteAddressById(Long addressId);
}
