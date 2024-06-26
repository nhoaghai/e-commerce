package com.ecommerce.controller.member;

import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.model.OrderStatus;
import com.ecommerce.domain.order.serviceImpl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member/history")
public class HistoryController {
    private final OrderServiceImpl orderService;


    @GetMapping("/list")
    public ResponseEntity<List<OrderResponse>> getOrderHistory(@RequestParam("status") OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrderHistory(status));
    }

    @GetMapping("/list")
    public ResponseEntity<List<OrderResponse>> getAllOrderHistory() {
        return ResponseEntity.ok(orderService.getAllOrderHistory());
    }

}
