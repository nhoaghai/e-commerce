package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.model.Order;
import com.ecommerce.domain.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    Page<Order> findAllByMemberMemberIdAndOrderStatusIsIn(String id, List<OrderStatus> orderStatus, Pageable pageable);

    List<Order> findAllByMemberMemberIdAndOrderStatusIsIn(String id, List<OrderStatus> orderStatus);

//    List<Order> findAllByMemberMemberId(String id);

    Order findFirstBySerialNumber(String serial);

    List<Order> findAllByOrderStatusAndMemberMemberId(OrderStatus orderStatus, String memberId);

    Page<Order> findAllByOrderStatusAndMemberMemberId(OrderStatus orderStatus, String memberId, Pageable pageable);

    List<Order> findAllByCreateAtBetweenAndMemberMemberIdAndOrderStatus(LocalDateTime first, LocalDateTime last, String memberId, OrderStatus status);
}
