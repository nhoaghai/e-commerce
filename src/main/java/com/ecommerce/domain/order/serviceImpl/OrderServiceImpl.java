package com.ecommerce.domain.order.serviceImpl;

import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.order.dto.request.OrderRequest;
import com.ecommerce.domain.order.dto.response.OrderDetailResponse;
import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.exception.OrderException;
import com.ecommerce.domain.order.model.Order;
import com.ecommerce.domain.order.model.OrderDetail;
import com.ecommerce.domain.order.model.OrderDetailId;
import com.ecommerce.domain.order.model.OrderStatus;
import com.ecommerce.domain.order.repository.OrderDetailRepository;
import com.ecommerce.domain.order.repository.OrderRepository;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import com.ecommerce.domain.shoppingCart.dto.request.CheckoutRequest;
import com.ecommerce.domain.shoppingCart.exception.CartException;
import com.ecommerce.domain.shoppingCart.model.ShoppingCart;
import com.ecommerce.domain.shoppingCart.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OrderResponse checkout(OrderRequest orderRequest) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Order order = new Order();
        order.setSerialNumber(UUID.randomUUID().toString());
        order.setNote(orderRequest.getNote());
        order.setReceiveAddress(orderRequest.getReceiveAddress());
        order.setReceiveName(orderRequest.getReceiveName());
        order.setReceivePhone(orderRequest.getReceivePhone());
        order.setOrderStatus(OrderStatus.WAITING);
        order.setCreateAt(LocalDateTime.now());
        order.setMember(memberRepository.findByMemberId(userDetails.getId()));

        orderRepository.save(order);
        order.setTotalPrice(BigDecimal.valueOf(1));
        ArrayList<CheckoutRequest> checkoutRequest = orderRequest.getCheckoutRequests();
        for (CheckoutRequest request : checkoutRequest) {
            ShoppingCart cart = cartRepository.findById(request.getShoppingCartId())
                    .orElseThrow(() -> CartException.notFound("Cannot found cart with that id!"));
            if(!Objects.equals(cart.getMember().getMemberId(), userDetails.getId())) {
                throw CartException.notFound("Cannot found cart with that id!");
            }

            Product product = productRepository.findByProductId(cart.getProduct().getProductId());

            OrderDetail orderDetail = new OrderDetail();
            if (request.getProductQuantity().equals(cart.getProductQuantity()))
                orderDetail.setProductQuantity(request.getProductQuantity());
            orderDetail.setProduct(product);
            OrderDetailId orderDetailId = new OrderDetailId(product.getProductId(), order.getOrderId());
            orderDetail.setOrder(order);
            orderDetail.setOrderDetailId(orderDetailId);
            orderDetail.setOrderDetailStatus(OrderStatus.WAITING);
            orderDetailRepository.save(orderDetail);

            order.setTotalPrice(product.getUnitPrice()
                    .multiply(BigDecimal.valueOf(request.getProductQuantity()))
                    .multiply(BigDecimal.valueOf(1 - (double) product.getDiscount()/100))
                    .add(order.getTotalPrice()));

            int newQuantity = cart.getProductQuantity() - orderDetail.getProductQuantity();
            if(newQuantity <= 0) {
                cartRepository.delete(cart);
            } else {
                cart.setProductQuantity(newQuantity);
            }
        }
        order.setTotalPrice(order.getTotalPrice().subtract(BigDecimal.valueOf(1)));
        orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

    public PageResponseDto<OrderResponse> findAllOrders(Pageable pageable) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<OrderStatus> statuses = Arrays.asList(OrderStatus.WAITING, OrderStatus.CONFIRM, OrderStatus.DELIVERY);
        Page<Order> page = orderRepository.findAllByMemberMemberIdAndOrderStatusIsIn(userDetails.getId(), statuses, pageable);

        List<OrderResponse> data = page.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();

        return PageResponseDto.<OrderResponse>builder()
                .data(data)
                .totalPage(page.getTotalPages())
                .pageNumber(page.getNumber())
                .size(page.getSize())
                .sort(page.getSort().toString())
                .build();

    }

    public List<OrderDetailResponse> getProductsInOrder(String serialNumber) {
        Order order = orderRepository.findFirstBySerialNumber(serialNumber);
        checkValidUser(order.getMember().getMemberId());
        List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);
        return orderDetailList.stream()
            .map(orderDetail -> {
                OrderDetailResponse response = new OrderDetailResponse();
                response.setProductQuantity(orderDetail.getProductQuantity());
                Product product = productRepository.findByProductId(orderDetail.getProduct().getProductId());
                response.setProductName(product.getProductName());
                response.setProductCategory(product.getCategory().getCategoryName());
                response.setProductDescription(product.getDescription());
                response.setProductDiscount(product.getDiscount());
                response.setProductPrice(product.getUnitPrice());
                response.setProductImageUrl(product.getImageUrl());
                response.setOrderDetailStatus(orderDetail.getOrderDetailStatus());
                return response;
            }).toList();
    }

    @Override
    @Transactional
    public OrderResponse confirmOrder(String sku) {
        Order order = orderRepository.findFirstBySerialNumber(sku);

        if(order == null) {
            throw OrderException.notFound("Cannot find order with that sku!");
        }

        checkValidUser(order.getMember().getMemberId());
        if(order.getOrderStatus() == OrderStatus.DELIVERY) {
            List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);
            for(OrderDetail orderDetail: orderDetailList) {
                orderDetail.setOrderDetailStatus(OrderStatus.SUCCESS);
                orderDetailRepository.save(orderDetail);
            }
            order.setOrderStatus(OrderStatus.SUCCESS);
            order.setReceiveAt(LocalDateTime.now());
            orderRepository.save(order);
        } else {
            throw OrderException.badRequest("Sorry, you cannot confirm this order. This order has not been delivered'.");
        }
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse cancelOrder(String sku) {
        Order order = orderRepository.findFirstBySerialNumber(sku);
        if(order == null) {
            throw OrderException.notFound("Cannot find order of this sku");
        }

        checkValidUser(order.getMember().getMemberId());
        if(order.getOrderStatus() == OrderStatus.WAITING) {
            List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);
            for(OrderDetail orderDetail: orderDetailList) {
                orderDetail.setOrderDetailStatus(OrderStatus.CANCEL);
                orderDetailRepository.save(orderDetail);

                // update stock quantity
                Product product = orderDetail.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderDetail.getProductQuantity());
                productRepository.save(product);
            }
            order.setOrderStatus(OrderStatus.CANCEL);
            orderRepository.save(order);
        } else {
            throw OrderException.badRequest("Sorry, you cannot cancel this order. This order has already been prepared.");
        }
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public List<OrderResponse> getOrderHistory(OrderStatus status) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> orderList = orderRepository.findAllByOrderStatusAndMemberMemberId(status, userDetails.getId());
        return orderList.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();
    }

    @Override
    public PageResponseDto<OrderResponse> getSuccessOrderHistory(Pageable pageable) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Order> page = orderRepository.findAllByOrderStatusAndMemberMemberId(OrderStatus.SUCCESS, userDetails.getId(), pageable);
        List<OrderResponse> data = page.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();

        return PageResponseDto.<OrderResponse>builder()
                .data(data)
                .totalPage(page.getTotalPages())
                .pageNumber(page.getNumber())
                .size(page.getSize())
                .sort(page.getSort().toString())
                .build();

    }
    @Override
    public List<OrderResponse> getCancelOrderHistory() {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.CANCEL, OrderStatus.DENIED);
        List<Order> orderList = orderRepository.findAllByMemberMemberIdAndOrderStatusIsIn(userDetails.getId(), statuses);
        return orderList.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();
    }


    @Override
    public BigDecimal getMonthlySpending(Integer month, Integer year) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        // Create a YearMonth instance for the specified year and month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDateTime firstDay = firstDayOfMonth.atStartOfDay();

        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        LocalDateTime lastDay = lastDayOfMonth.atTime(23, 59, 59, 999999999);

        BigDecimal result = BigDecimal.valueOf(0);

        List<Order> orderList = orderRepository.findAllByCreateAtBetweenAndMemberMemberIdAndOrderStatus(firstDay, lastDay, userDetails.getId(), OrderStatus.CONFIRM);
        for(Order order:orderList) {
            result = result.add(order.getTotalPrice());
        }
        return result;
    }


    public void checkValidUser(String memberId) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!Objects.equals(memberId, userDetails.getId())) {
            throw OrderException.notFound("Cannot find this order in your account, please retry");
        }
    }
}

