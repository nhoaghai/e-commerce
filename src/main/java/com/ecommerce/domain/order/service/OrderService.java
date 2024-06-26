package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.dto.request.OrderRequest;
import com.ecommerce.domain.order.dto.response.OrderDetailResponse;
import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse checkout(OrderRequest orderRequest);

    List<OrderResponse> findAllOrders();

    OrderResponse cancelOrder(String sku);

    List<OrderDetailResponse> getProductsInOrder(String sku);

    OrderResponse confirmOrder(String sku);

    List<OrderResponse> getOrderHistory(OrderStatus status);

    List<OrderResponse> getAllOrderHistory();
}


