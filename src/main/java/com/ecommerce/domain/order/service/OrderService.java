package com.ecommerce.domain.order.service;

import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.order.dto.request.OrderRequest;
import com.ecommerce.domain.order.dto.response.OrderDetailResponse;
import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.model.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    OrderResponse checkout(OrderRequest orderRequest);

    PageResponseDto<OrderResponse> findAllOrders(Pageable pageable);

    OrderResponse cancelOrder(String sku);

    List<OrderDetailResponse> getProductsInOrder(String sku);

    OrderResponse confirmOrder(String sku);

    List<OrderResponse> getOrderHistory(OrderStatus status);

    PageResponseDto<OrderResponse> getSuccessOrderHistory(Pageable pageable);

    List<OrderResponse> getCancelOrderHistory();

    BigDecimal getMonthlySpending(Integer month, Integer year);
}


