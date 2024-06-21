package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.model.OrderDetail;
import com.ecommerce.domain.order.model.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}
