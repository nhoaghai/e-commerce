package com.ecommerce.domain.order.serviceImpl;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.domain.order.dto.request.OrderRequest;
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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public OrderResponse checkout(OrderRequest orderRequest) {
        Order order = new Order();
        order.setSerialNumber(UUID.randomUUID().toString());
        order.setNote(orderRequest.getNote());
        order.setReceiveAddress(orderRequest.getReceiveAddress());
        order.setReceiveName(orderRequest.getReceiveName());
        order.setReceivePhone(orderRequest.getReceivePhone());
        order.setOrderStatus(OrderStatus.WAITING);
        order.setCreateAt(LocalDateTime.now());
        order.setReceiveAt(LocalDateTime.now());
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        order.setMember(memberRepository.findByMemberId(userDetails.getId()));

        orderRepository.save(order);
        order.setTotalPrice(BigDecimal.valueOf(1));
        ArrayList<CheckoutRequest> checkoutRequest = orderRequest.getCheckoutRequests();
        for (CheckoutRequest request : checkoutRequest) {
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setProductQuantity(request.getProductQuantity());
            ShoppingCart cart = cartRepository.findById(request.getShoppingCartId())
                    .orElseThrow(() -> DomainException.notFound("Not found cart with that id!"));
            Product product = productRepository.findByProductId(cart.getProduct().getProductId());

            orderDetail.setProduct(product);
            OrderDetailId orderDetailId = new OrderDetailId(product.getProductId(), order.getOrderId());
            orderDetail.setOrder(order);
            orderDetail.setOrderDetailId(orderDetailId);
            orderDetailRepository.save(orderDetail);
            order.setTotalPrice(product.getUnitPrice().multiply(BigDecimal.valueOf(request.getProductQuantity())).add(order.getTotalPrice()));
        }
        order.setTotalPrice(order.getTotalPrice().subtract(BigDecimal.valueOf(1)));
        orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

    public OrderResponse findAllOrders() {
        return null;
    }
}
