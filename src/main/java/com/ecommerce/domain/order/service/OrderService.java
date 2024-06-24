package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.dto.request.OrderRequest;
import com.ecommerce.domain.order.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse checkout(OrderRequest orderRequest);

    OrderResponse findAllOrders();
}


