package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.order.OrderRequest;
import com.tanduydev.ecommerce.dto.request.order.OrderSearchRequest;
import com.tanduydev.ecommerce.dto.response.PagedResult;
import com.tanduydev.ecommerce.dto.response.order.OrderResponse;
import com.tanduydev.ecommerce.enums.OrderStatus;
import com.tanduydev.ecommerce.mapper.OrderMapper;
import com.tanduydev.ecommerce.model.*;
import com.tanduydev.ecommerce.repository.*;
import com.tanduydev.ecommerce.repository.specification.OrderSpecification;
import com.tanduydev.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final CouponRepository couponRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(String email, OrderRequest request) {
        Customer customer = (Customer) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Cart cart = cartRepository.findByCustomer_Email(email)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getCustomer().getEmail().equals(email)) {
            throw new RuntimeException("Invalid address");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setShippingAddress(address.getDetailAddress());
        order.setNote(request.getNote());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);

        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String shortUuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        order.setOrderCode("ORD-" + timeStamp + "-" + shortUuid);

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            ProductVariant variant = cartItem.getProductVariant();

            if (cartItem.getQuantity() > variant.getStock()) {
                throw new IllegalArgumentException("Product " + variant.getProduct().getName() + " does not have enough stock");
            }

            variant.setStock(variant.getStock() - cartItem.getQuantity());
            variantRepository.save(variant);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(variant);
            orderItem.setSku(variant.getSku());
            orderItem.setAttributesCombination(variant.getAttributesCombination());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(variant.getPrice());

            BigDecimal itemTotal = variant.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            orderItem.setTotalPrice(itemTotal);
            orderItems.add(orderItem);

            totalPrice = totalPrice.add(itemTotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        BigDecimal shippingFee = new BigDecimal("30000");
        order.setShippingFee(shippingFee);

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid coupon code"));

            discountAmount = totalPrice.multiply(new BigDecimal("0.1"));
            order.setCoupon(coupon);
        }
        order.setDiscountAmount(discountAmount);

        BigDecimal grandTotal = totalPrice.add(shippingFee).subtract(discountAmount);
        order.setGrandTotal(grandTotal.max(BigDecimal.ZERO));

        Order savedOrder = orderRepository.save(order);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        log.info("[ORDER] Customer {} created order {}", email, savedOrder.getOrderCode());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        List<Order> orders = orderRepository.findAllByCustomer_EmailOrderByCreatedAtDesc(email);
        return orderMapper.toResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCode(String email, String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getEmail().equals(email)) {
            throw new RuntimeException("You do not have permission to view this order");
        }

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot change status of a cancelled order");
        }

        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getOrderItems()) {
                ProductVariant variant = item.getProductVariant();
                variant.setStock(variant.getStock() + item.getQuantity());
                variantRepository.save(variant);
            }
            log.info("[ORDER] Restored stock for cancelled order: {}", order.getOrderCode());
        }

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        log.info("[ORDER] Admin updated order {} to status {}", order.getOrderCode(), newStatus);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<OrderResponse> getAllOrders(OrderSearchRequest filter, Pageable pageable) {
        log.info("[ORDER] Admin is fetching orders with pagination and filters");
        Page<Order> ordersPage = orderRepository.findAll(OrderSpecification.withFilter(filter), pageable);
        return new PagedResult<>(ordersPage.map(orderMapper::toResponse));
    }
}