package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.model.Order;
import com.ecommerce.domain.order.model.OrderDetail;
import com.ecommerce.domain.order.model.OrderDetailId;
import com.ecommerce.domain.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    List<OrderDetail> findAllByOrder(Order order);

    //Optional<OrderDetail> findAllByOrder(Order order);

    List<OrderDetail> findAllByProductProductIdInAndOrderOrderStatus(List<Long> productIds, OrderStatus status);

    Page<OrderDetail> findAllByProductProductIdIn(List<Long> productIds, Pageable pageable);

    Page<OrderDetail> findAllByProductProductIdInAndOrderDetailStatus(List<Long> productIds, OrderStatus status, Pageable pageable);
}

