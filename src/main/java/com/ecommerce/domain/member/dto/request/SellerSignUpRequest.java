package com.ecommerce.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerSignUpRequest {
    @NotBlank(message = "Please enter your shop name")
    private String shopName;
    @NotBlank(message = "Please enter your address pickup")
    private String addressPickup;
    @NotBlank(message = "Please enter your email")
    private String email;
    @NotBlank(message = "Please enter your phone number")
    private String phoneNumber;
}
