package com.ecommerce.domain.member.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.member.dto.request.AddressRequest;
import com.ecommerce.domain.member.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAllAddressList();

    MessageResponse addNewAddress(AddressRequest addressRequest);

    List<AddressResponse> changeAddress(AddressRequest addressRequest, Long addressId);

    AddressResponse findAddressById(Long addressId);

    MessageResponse deleteAddressById(Long addressId);
}
