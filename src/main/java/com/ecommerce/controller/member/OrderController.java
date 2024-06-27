package com.ecommerce.controller.member;

import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.order.dto.response.OrderDetailResponse;
import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.serviceImpl.OrderServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/member")
@SecurityRequirement(name = "Bearer Authentication")

public class OrderController {
    private final OrderServiceImpl orderService;

    @GetMapping("/orders")
    public ResponseEntity<PageResponseDto<OrderResponse>> findAllOrders(@SortDefault(sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.findAllOrders(pageable));
    }

    @PutMapping("/orders/cancel/{sku}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String sku) {
        return ResponseEntity.ok(orderService.cancelOrder(sku));
    }

    @PutMapping("/orders/confirm/{sku}")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable String sku) {
        return ResponseEntity.ok(orderService.confirmOrder(sku));
    }

    @GetMapping("/orders/order-detail/{sku}")
    public ResponseEntity<List<OrderDetailResponse>> getOrderDetail(@PathVariable String sku) {
        return ResponseEntity.ok(orderService.getProductsInOrder(sku));
    }

}
