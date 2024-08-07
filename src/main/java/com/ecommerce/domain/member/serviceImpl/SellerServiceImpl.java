package com.ecommerce.domain.member.serviceImpl;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.member.dto.request.SellerProductRequest;
import com.ecommerce.domain.member.dto.request.SellerSignUpRequest;
import com.ecommerce.domain.member.exception.SellerException;
import com.ecommerce.domain.member.model.Seller;
import com.ecommerce.domain.member.repository.SellerRepository;
import com.ecommerce.domain.member.service.SellerService;
import com.ecommerce.domain.order.dto.response.SellerOrderDetailResponse;
import com.ecommerce.domain.order.exception.OrderException;
import com.ecommerce.domain.order.model.Order;
import com.ecommerce.domain.order.model.OrderDetail;
import com.ecommerce.domain.order.model.OrderDetailId;
import com.ecommerce.domain.order.model.OrderStatus;
import com.ecommerce.domain.order.repository.OrderDetailRepository;
import com.ecommerce.domain.order.repository.OrderRepository;
import com.ecommerce.domain.product.dto.request.ProductRequest;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import com.ecommerce.domain.product.exception.CategoryException;
import com.ecommerce.domain.product.exception.ProductException;
import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.product.repository.CategoryRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.security.exception.MemberException;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.model.Role;
import com.ecommerce.domain.security.model.RoleName;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.serviceImpl.RoleServiceImpl;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.xml.catalog.CatalogException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RoleServiceImpl roleService;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public MessageResponse sellerSignUp(SellerSignUpRequest sellerSignUpRequest) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId()).orElseThrow(()-> new MemberException("Member not found!"));
        if (member == null) {
            throw MemberException.notFound("No member found");
        }

        Role roleSeller = roleService.findByRoleName(RoleName.ROLE_SELLER);
        if (member.getRoles().contains(roleSeller)) {
            throw SellerException.badRequest("Member already is seller");
        }
        member.getRoles().add(roleSeller);
        memberRepository.save(member);

        Seller seller = new Seller();
        seller.setShopName(sellerSignUpRequest.getShopName());
        seller.setEmail(sellerSignUpRequest.getEmail());
        seller.setAddressPickUp(sellerSignUpRequest.getAddressPickup());
        seller.setPhoneNumber(sellerSignUpRequest.getPhoneNumber());
        seller.setActive(true);
        seller.setMember(member);
        sellerRepository.save(seller);
        return MessageResponse.builder()
                .httpStatus(HttpStatus.CREATED)
                .message("Register to seller successfully!")
                .build();
    }

    @Override
    public MessageResponse sellerSignIn() {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId()).orElseThrow(()-> new MemberException("Member not found!"));
        if (member == null) {
            throw MemberException.notFound("No member found");
        }

        if (!member.getRoles().contains(roleService.findByRoleName(RoleName.ROLE_SELLER))) {
            throw new SellerException("Member did not register to be a seller!");
        }

        Seller seller = sellerRepository.findByMemberMemberId(memberDetail.getId())
                .orElseThrow(() -> new SellerException("Seller not found!"));

        if (!seller.isActive()) {
            throw new SellerException("Member was banned! Contact to admin to unlock!");
        } else {
            return MessageResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Hello " + seller.getShopName())
                    .build();
        }
    }

    @Override
    public PageResponseDto<ProductResponse> findAllSellingProduct(Pageable pageable) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Product> page = productRepository.findAllBySellerSellerId(userDetails.getId(), pageable);

        if (page.isEmpty()) {
            throw ProductException.notFound("No products is being sold!");
        } else {
            List<ProductResponse> data = page.stream()
                    .map(order -> modelMapper.map(order, ProductResponse.class))
                    .toList();
            return PageResponseDto.<ProductResponse>builder()
                    .data(data)
                    .totalPage(page.getTotalPages())
                    .pageNumber(page.getNumber())
                    .size(page.getSize())
                    .sort(page.getSort().toString())
                    .build();
        }
    }

    @Override
    public ProductResponse findSellingProduct(String sku) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product product = productRepository.findByProductSku(sku);
        if (product == null) {
            throw ProductException.notFound("No product with sku " + sku + " found");
        }

        if (!Objects.equals(userDetails.getId(), product.getSeller().getSellerId())) {
            throw ProductException.forbidden("You are not authorized to view this product");
        }
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public ProductResponse addProduct(ProductRequest request) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Product> productList = productRepository.findAllBySellerSellerId(userDetails.getId());

        // Check existing products for overlap with requested product (when the shop HAS any product)
        if (!productList.isEmpty()) {
            for (Product product : productList) {
                if (Objects.equals(product.getProductName(), request.getProductName())) {
                    throw ProductException.conflict("You already have a product of this name. Please choose another product name");
                }
            }
        }

        Product product = modelMapper.map(request, Product.class);
        product.setActive(true);
        product.setSku(generateRandomSKU());
        Seller seller = sellerRepository.findByMemberMemberId(userDetails.getId())
                .orElseThrow(() -> new SellerException("Seller not found!"));
        product.setSeller(seller);
        product.setCreateAt(LocalDateTime.now());
        product.setCategory(categoryRepository.findByCategoryName(request.getCategoryName())
                .orElseThrow(() -> new CategoryException("Category not found!")));

        productRepository.save(product);

        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        response.setShopName(seller.getShopName());
        return response;
    }

    public static String generateRandomSKU() {
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();

        String part1 = uuidStr.substring(0, 8);
        String part2 = uuidStr.substring(9, 13);
        String part3 = uuidStr.substring(14, 18);

        return part1 + "-" + part2 + "-" + part3;
    }

    @Override
    public ProductResponse editProduct(Long productId, SellerProductRequest request) {
        Product product = checkProductOwnership(productId);
        if (product == null) {
            throw SellerException.notFound("No such product in your shop or wrong sellerId");
        } else {
            if (request.getProductName() != null)
                product.setProductName(request.getProductName());
            if (request.getDescription() != null)
                product.setDescription(request.getDescription());
            if (request.getStockQuantity() != null)
                product.setStockQuantity(request.getStockQuantity());
            if (request.getUnitPrice() != null)
                product.setUnitPrice(request.getUnitPrice());
            if (request.getImageUrl() != null)
                product.setImageUrl(request.getImageUrl());
            if (request.getDiscount() != null)
                product.setDiscount(request.getDiscount());
            if (request.getShopName() != null) {
                Seller seller = product.getSeller();
                if (seller != null) {
                    seller.setShopName(request.getShopName());
                } else {
                    throw SellerException.notFound("No seller associated with this product");
                }
            }
            productRepository.save(product);
        }
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        response.setShopName(product.getSeller()
                .getShopName());
        return response;
    }

    public MessageResponse deleteProduct(Long productId) {
        Product product = checkProductOwnership(productId);
        if (product == null) {
            throw SellerException.notFound("No such product in your shop or wrong sellerId");
        } else {
            productRepository.delete(product);
            return MessageResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Deleted " + product.getProductName() + " with product ID " + productId + " successfully")
                    .build();
        }
    }

    @Override
    // get all orders of a shop
    public PageResponseDto<SellerOrderDetailResponse> getAllOrders(Pageable pageable) {
        List<Long> productIds = getProductIds();
        Page<OrderDetail> orderDetailsPage = orderDetailRepository.findAllByProductProductIdIn(productIds, pageable);

        if (orderDetailsPage == null || orderDetailsPage.isEmpty()) {
            throw OrderException.notFound("No orders found for your products");
        }

        List<SellerOrderDetailResponse> orderDetailResponses = orderDetailsPage.getContent().stream()
                .map(orderDetail -> {
                    SellerOrderDetailResponse odRsp = modelMapper.map(orderDetail, SellerOrderDetailResponse.class);
                    odRsp.setStatus(getOrderDetailStatus(orderDetail));
                    return odRsp;
                })
                .toList();

        PageResponseDto<SellerOrderDetailResponse> pageResponseDto = new PageResponseDto<>();
        pageResponseDto.setData(orderDetailResponses);
        pageResponseDto.setTotalPage(orderDetailsPage.getTotalPages());
        pageResponseDto.setPageNumber(orderDetailsPage.getNumber());
        pageResponseDto.setSort(orderDetailsPage.getSort().toString());
        pageResponseDto.setSize(orderDetailsPage.getSize());

        return pageResponseDto;
    }

    @Override
    // get OD by Id
    public List<SellerOrderDetailResponse> getOrderById(Long orderId) {
        List<OrderDetail> orderDetails = getOrderByIdFromSeller(orderId);
        if (orderDetails.isEmpty()) {
            throw OrderException.notFound("Your shop has no order");
        }
        // ALWAYS REMEMBER TO SET ORDER STATUS
        return orderDetails.stream()
                .map(orderDetail -> {
                    SellerOrderDetailResponse odResponse = modelMapper.map(orderDetail, SellerOrderDetailResponse.class);
                    odResponse.setStatus(getOrderDetailStatus(orderDetail));
                    return odResponse;
                })
                .toList();
    }

    // Get all DELIVERED orders
    @Override
    public PageResponseDto<SellerOrderDetailResponse> getOrderByStatus(OrderStatus status, Pageable pageable) {
        List<Long> productIds = getProductIds();
        Page<OrderDetail> orderDetailPage = orderDetailRepository.findAllByProductProductIdInAndOrderDetailStatus(productIds, status, pageable);

        if (orderDetailPage.isEmpty()) {
            throw OrderException.notFound("No orders found for your products with status " + status);
        }

        List<SellerOrderDetailResponse> orderDetailResponses = orderDetailPage.getContent().stream()
                .map(orderDetail -> {
                    SellerOrderDetailResponse odResponse = modelMapper.map(orderDetail, SellerOrderDetailResponse.class);
                    odResponse.setStatus(status);
                    return odResponse;
                }).toList();

        PageResponseDto<SellerOrderDetailResponse> pageResponseDto = new PageResponseDto<>();
        pageResponseDto.setData(orderDetailResponses);
        pageResponseDto.setTotalPage(orderDetailPage.getTotalPages());
        pageResponseDto.setPageNumber(orderDetailPage.getNumber());
        pageResponseDto.setSort(orderDetailPage.getSort().toString());
        pageResponseDto.setSize(orderDetailPage.getSize());

        return pageResponseDto;
    }

    public MessageResponse getRevenueOverTime(String startDateStr, String endDateStr) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            List<Long> productIds = getProductIds();
            List<OrderDetail> allSellerFinalizedOrders = orderDetailRepository.findAllByProductProductIdInAndOrderOrderStatus(productIds, OrderStatus.SUCCESS);

            if (allSellerFinalizedOrders.isEmpty())
                throw OrderException.notFound("You have no finalized order to calculate revenue");

            BigDecimal revenueOverTime = BigDecimal.ZERO;
            for (OrderDetail ord : allSellerFinalizedOrders) {
                LocalDateTime receiveAt = ord.getOrder().getReceiveAt();

                if (receiveAt != null) {
                    LocalDate receiveDate = LocalDate.from(ord.getOrder().getReceiveAt());

                    if (receiveDate.isAfter(startDate.minusDays(1))
                            && receiveDate.isBefore(endDate.plusDays(1))) {
                        BigDecimal orderDetailPrice = ord.getProduct().getUnitPrice()
                                .multiply(BigDecimal.valueOf(ord.getProductQuantity()))
                                .multiply(BigDecimal.valueOf(1 - (double) ord.getProduct().getDiscount() / 100));
                        revenueOverTime = revenueOverTime.add(orderDetailPrice);
                    }
                }
            }

            return MessageResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Your revenue from " + startDate + " to " + endDate + " is " + revenueOverTime + " VND")
                    .build();
        } catch (DateTimeParseException e) {
            // Handle the exception - create a custom message response
            return new MessageResponse(HttpStatus.BAD_REQUEST, "Invalid date format. The supported format is yyyy-mm-dd", null);
        }
    }

    @Override
    public SellerOrderDetailResponse editOrderDetailStatus(OrderDetailId odID, OrderStatus odStatus) {
        OrderDetail orderDetail = orderDetailRepository.findById(odID)
                .orElseThrow(() -> OrderException.notFound("No seller order found"));

        // Update sub-order's status
        orderDetail.setOrderDetailStatus(odStatus);
        orderDetailRepository.save(orderDetail);


        // DENY: update order's total price
        if (odStatus.equals(OrderStatus.DENIED)) {
            Order order = orderRepository.findById(odID.getOrderId())
                    .orElseThrow(() -> OrderException.notFound("No order found"));

            Product product = orderDetail.getProduct();
            if (product == null) {
                throw ProductException.notFound("The product for this order doesn't exist. " +
                        "Likely out of stock. Contact seller for more info.");
            }

            BigDecimal deniedOrderPrice = product.getUnitPrice()
                    .multiply(BigDecimal.valueOf(orderDetail.getProductQuantity()))
                    .multiply(BigDecimal.valueOf(1 - (product.getDiscount() / 100)));

            order.setTotalPrice(order.getTotalPrice().subtract(deniedOrderPrice));
            orderRepository.save(order);
        }

        // Synchronize the total order's status when all order details are of the same stt
        syncAllStatus(odID, odStatus);

        return modelMapper.map(orderDetail, SellerOrderDetailResponse.class);
    }

    private List<Long> getProductIds() {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Product> products = productRepository.findAllBySellerSellerId(userDetails.getId());
        if (userDetails.getId() == null) {
            throw MemberException.notFound(" no mem");
        }
        if (products.isEmpty()) {
            throw ProductException.notFound("Your shop has no product");
        }

        return products.stream()
                .map(Product::getProductId)
                .toList();
    }

    private OrderStatus getOrderDetailStatus(OrderDetail od) {
        OrderStatus oddStatus = od.getOrderDetailStatus();
        if (oddStatus == null) {
            throw OrderException.notFound("Your seller order doesn't have any status");
        }
        return oddStatus;
    }

    private Product checkProductOwnership(Long productId) {
        UserDetailImpl memberDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByMemberId(memberDetail.getId()).orElseThrow(()-> new MemberException("Member not found!"));
        return productRepository.findBySellerSellerIdAndProductId(member.getMemberId(), productId).orElseThrow(() -> new ProductException("Product not found!"));
    }

    private List<OrderDetail> getOrderByIdFromSeller(Long orderId) {
        UserDetailImpl userDetails = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Find the order by ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> OrderException.notFound("No order found with id " + orderId));

        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);
        if (orderDetails.isEmpty())
            throw OrderException.notFound("Order details not found for order with id " + orderId);

        // Filter out only the ODs where the product belongs to the seller
        List<OrderDetail> sellerOrderDetails = orderDetails.stream()
                .filter(orderDetail -> orderDetail.getProduct().getSeller().getSellerId().equals(userDetails.getId()))
                .toList();

        if (sellerOrderDetails.isEmpty())
            throw OrderException.forbidden("Unauthorized to view this order: no product here belongs to your shop");

        return sellerOrderDetails;
    }

    private void syncAllStatus(OrderDetailId odID, OrderStatus statusToCheck) {
        Order order = orderRepository.findById(odID.getOrderId())
                .orElseThrow(() -> OrderException.notFound("No order found"));
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);

        boolean allSameStatus = true;
        for (OrderDetail od : orderDetails) {
            if (!getOrderDetailStatus(od).equals(statusToCheck)) {
                allSameStatus = false;
                break;
            }
        }

        if (allSameStatus) {
            order.setOrderStatus(statusToCheck);
            orderRepository.save(order);
        }
    }

}
