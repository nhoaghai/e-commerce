package com.ecommerce.domain.security.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeAddressRequest {
    private String fullAddress;
    private String phoneNumber;
    private String receiveName;
}
