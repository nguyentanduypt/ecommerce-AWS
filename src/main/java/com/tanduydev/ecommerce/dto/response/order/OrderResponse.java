package com.tanduydev.ecommerce.dto.response.order;

import com.tanduydev.ecommerce.enums.OrderStatus;
import com.tanduydev.ecommerce.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private String orderCode;

    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String note;

    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal grandTotal;

    private OrderStatus status;
    private PaymentMethod paymentMethod;

    private List<OrderItemResponse> orderItems;

    private LocalDateTime createdAt;
}