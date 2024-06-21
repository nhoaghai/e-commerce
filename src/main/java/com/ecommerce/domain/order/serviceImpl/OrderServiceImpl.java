package com.ecommerce.domain.order.serviceImpl;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.domain.order.dto.request.OrderRequest;
import com.ecommerce.domain.order.dto.response.OrderResponse;
import com.ecommerce.domain.order.model.Order;
import com.ecommerce.domain.order.model.OrderDetail;
import com.ecommerce.domain.order.model.OrderStatus;
import com.ecommerce.domain.order.repository.OrderDetailRepository;
import com.ecommerce.domain.order.repository.OrderRepository;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.shopingCart.model.ShoppingCart;
import com.ecommerce.domain.shopingCart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    @Override
    public OrderResponse checkout(OrderRequest orderRequest) {
        Order order = new Order();
        orderRepository.save(order);
        order.setTotalPrice(BigDecimal.valueOf(1));
        orderRequest.getCheckoutRequests().forEach(request -> {
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setProductQuantity(request.getProductQuantity());
            ShoppingCart cart = cartRepository.findById(request.getShoppingCartId())
                    .orElseThrow(()-> DomainException.notFound("Not found cart with that id!"));
            Product product = productRepository.findByProductId(cart.getProduct().getProductId());
            orderDetail.setProduct(product);
            orderDetail.setOrder(order);
            orderDetailRepository.save(orderDetail);
            order.setTotalPrice(product.getUnitPrice().multiply(BigDecimal.valueOf(request.getProductQuantity())).add(order.getTotalPrice()));
        });
        order.setSerialNumber(UUID.randomUUID().toString());
        order.setNote(orderRequest.getNote());
        order.setReceiveAddress(orderRequest.getReceiveAddress());
        order.setReceiveName(orderRequest.getReceiveName());
        order.setReceivePhone(orderRequest.getReceivePhone());
        order.setOrderStatus(OrderStatus.WAITING);
        order.setTotalPrice(order.getTotalPrice().subtract(BigDecimal.valueOf(1)));
        orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }
}
