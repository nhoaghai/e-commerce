package com.ecommerce.controller.member;

import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.model.OrderStatus;
import com.ecommerce.domain.order.serviceImpl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member/history")
public class HistoryController {
    private final OrderServiceImpl orderService;


    @GetMapping("/{status}")
    public ResponseEntity<List<OrderResponse>> getOrderHistory(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrderHistory(status));
    }

    @GetMapping("")
    public ResponseEntity<PageResponseDto<OrderResponse>> getSuccessOrderHistory(Pageable pageable) {
        return ResponseEntity.ok(orderService.getSuccessOrderHistory(pageable));
    }

    @GetMapping("/cancel")
    public ResponseEntity<List<OrderResponse>> getCancelOrderHistory() {
        return ResponseEntity.ok(orderService.getCancelOrderHistory());
    }

    @GetMapping("/spending")
    public ResponseEntity<BigDecimal> getMonthlySpending(@RequestParam("month") Integer month,
                                                         @RequestParam("year") Integer year) {
        return ResponseEntity.ok(orderService.getMonthlySpending(month, year));
    }



}
