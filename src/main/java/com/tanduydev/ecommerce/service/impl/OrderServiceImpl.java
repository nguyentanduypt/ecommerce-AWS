package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.order.OrderRequest;
import com.tanduydev.ecommerce.dto.response.order.OrderResponse;
import com.tanduydev.ecommerce.enums.OrderStatus;
import com.tanduydev.ecommerce.mapper.OrderMapper;
import com.tanduydev.ecommerce.model.*;
import com.tanduydev.ecommerce.repository.*;
import com.tanduydev.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Order order = new Order();
        order.setCustomer(customer);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setNote(request.getNote());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);

        // Sinh mã đơn hàng ngẫu nhiên (VD: ORD-20260708-XYZ)
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String shortUuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        order.setOrderCode("ORD-" + timeStamp + "-" + shortUuid);

        // Xử lý các OrderItem và Tính tổng tiền
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            ProductVariant variant = cartItem.getProductVariant();

            // 1. Kiểm tra lại tồn kho một lần nữa trước khi chốt đơn
            if (cartItem.getQuantity() > variant.getStock()) {
                throw new IllegalArgumentException("Product " + variant.getProduct().getName() + " does not have enough stock");
            }

            // 2. Trừ tồn kho dưới DB
            variant.setStock(variant.getStock() - cartItem.getQuantity());
            variantRepository.save(variant);

            // 3. Tạo OrderItem
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

        // Tính phí ship (Giả sử fix cứng 30,000 VND, thực tế có thể gọi API Giao Hàng Nhanh)
        BigDecimal shippingFee = new BigDecimal("30000");
        order.setShippingFee(shippingFee);

        // Xử lý Coupon (Nếu có truyền mã)
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid coupon code"));

            // Kiểm tra tính hợp lệ của Coupon (Giả sử bạn có các logic isActive, minPurchase...)
            // Ở đây mình ví dụ trừ 10%
            discountAmount = totalPrice.multiply(new BigDecimal("0.1"));
            order.setCoupon(coupon);
        }
        order.setDiscountAmount(discountAmount);

        // Tổng thu cuối cùng
        BigDecimal grandTotal = totalPrice.add(shippingFee).subtract(discountAmount);
        order.setGrandTotal(grandTotal.max(BigDecimal.ZERO)); // Đảm bảo không bị số âm

        // Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);

        // Xóa sạch giỏ hàng
        cart.getCartItems().clear();
        cartRepository.save(cart);

        log.info("[ORDER] Customer {} created order {}", email, savedOrder.getOrderCode());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(String email) {
        List<Order> orders = orderRepository.findAllByCustomer_EmailOrderByCreatedAtDesc(email);
        return orderMapper.toResponseList(orders);
    }

    @Override
    public OrderResponse getOrderByCode(String email, String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getEmail().equals(email)) {
            throw new RuntimeException("You do not have permission to view this order");
        }

        return orderMapper.toResponse(order);
    }
}