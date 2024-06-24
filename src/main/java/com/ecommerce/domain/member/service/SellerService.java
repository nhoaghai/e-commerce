package com.ecommerce.domain.member.service;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.domain.member.dto.request.SellerSignUpRequest;
public interface SellerService {
    MessageResponse sellerSignUp(SellerSignUpRequest sellerSignUpRequest);

    MessageResponse sellerSignIn();
}
