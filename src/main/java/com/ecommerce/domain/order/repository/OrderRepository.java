package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.model.Order;
import com.ecommerce.domain.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByMemberMemberId(String id);

    Order findFirstBySerialNumber(String serial);

    List<Order> findAllByOrderStatusAndMemberMemberId(OrderStatus orderStatus, String memberId);
}
