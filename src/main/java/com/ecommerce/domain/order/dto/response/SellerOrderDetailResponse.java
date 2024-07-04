package com.ecommerce.domain.order.dto.response;

import com.ecommerce.domain.order.model.OrderDetailId;
import com.ecommerce.domain.order.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SellerOrderDetailResponse {
    private OrderStatus status;

    private OrderDetailId orderDetailId;

    private Integer productQuantity;

    private String productName;

    private BigDecimal productPrice;

    private String productDescription;

    private String productImageUrl;

    private String productCategory;

    private Integer productDiscount;
}
