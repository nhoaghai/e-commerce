package com.ecommerce.domain.order.serviceImpl;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.domain.order.dto.request.OrderRequest;
import com.ecommerce.domain.order.dto.response.OrderDetailResponse;
import com.ecommerce.domain.order.dto.response.OrderResponse;
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
import com.ecommerce.domain.shoppingCart.model.ShoppingCart;
import com.ecommerce.domain.shoppingCart.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
                    .orElseThrow(() -> DomainException.notFound("Cannot found cart with that id!"));
            if(!Objects.equals(cart.getMember().getMemberId(), userDetails.getId())) {
                throw new DomainException("Cannot found cart with that id!");
            };

            Product product = productRepository.findByProductId(cart.getProduct().getProductId());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProductQuantity(request.getProductQuantity());
            orderDetail.setProduct(product);
            OrderDetailId orderDetailId = new OrderDetailId(product.getProductId(), order.getOrderId());
            orderDetail.setOrder(order);
            orderDetail.setOrderDetailId(orderDetailId);
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

    public List<OrderResponse> findAllOrders() {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> orders = orderRepository.findAllByMemberMemberId(userDetails.getId());
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();
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
                return response;
            }).toList();
    }

    @Override
    public OrderResponse confirmOrder(String sku) {
        Order order = orderRepository.findFirstBySerialNumber(sku);
        checkValidUser(order.getMember().getMemberId());
        if(order.getOrderStatus() == OrderStatus.DELIVERY) {
            order.setOrderStatus(OrderStatus.CONFIRM);
            order.setReceiveAt(LocalDateTime.now());
            orderRepository.save(order);
        } else {
            throw new DomainException("Sorry, you cannot confirm this order. This order has not been delivered'.");
        }
        return modelMapper.map(order, OrderResponse.class);
    }

    public OrderResponse cancelOrder(String sku) {
        Order order = orderRepository.findFirstBySerialNumber(sku);
        checkValidUser(order.getMember().getMemberId());
        if(order.getOrderStatus() == OrderStatus.WAITING) {
            order.setOrderStatus(OrderStatus.CANCEL);
            orderRepository.save(order);
        } else {
            throw new DomainException("Sorry, you cannot cancel this order. This order has already been prepared.");
        }
        return modelMapper.map(order, OrderResponse.class);
    }

    public List<OrderResponse> getOrderHistory(OrderStatus status) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> orderList = orderRepository.findAllByOrderStatusAndMemberMemberId(status, userDetails.getId());
        return orderList.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();
    }

    @Override
    public List<OrderResponse> getAllOrderHistory() {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> orderList = orderRepository.findAllByMemberMemberId(userDetails.getId());
        return orderList.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();
    }

    public void checkValidUser(String memberId) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!Objects.equals(memberId, userDetails.getId())) {
            throw new DomainException("Cannot find this order in your account, please retry");
        }
    }
}

