package com.ecommerce.domain.order.dto.request;

import com.ecommerce.domain.shopingCart.dto.request.CheckoutRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private ArrayList<CheckoutRequest> checkoutRequests;
    private String note;
    private String receiveAddress;
    private String receiveName;
    private String receivePhone;
}
