package com.ecommerce.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String serialNumber;
    private String status;
    private String note;
    private String receiveAddress;
    private String receiveName;
    private String receivePhone;
    private LocalDateTime createAt;
    private BigDecimal totalPrice;
}
