package com.ecommerce.controller.member;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.member.dto.request.AddressRequest;
import com.ecommerce.domain.member.dto.response.AddressResponse;
import com.ecommerce.domain.member.serviceImpl.AddressServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member/address")
@SecurityRequirement(name = "Bearer Authentication")
public class AddressController {

    private final AddressServiceImpl addressService;

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddressList(){
        return ResponseEntity.ok(addressService.getAllAddressList());
    }

    @PostMapping
    public ResponseEntity<MessageResponse> addNewAddress(AddressRequest addressRequest){
        return ResponseEntity.ok(addressService.addNewAddress(addressRequest));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> findAddressById(@PathVariable Long addressId){
        return ResponseEntity.ok(addressService.findAddressById(addressId));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<MessageResponse> deleteAddressById(@PathVariable Long addressId){
        return ResponseEntity.ok(addressService.deleteAddressById(addressId));
    }
}
