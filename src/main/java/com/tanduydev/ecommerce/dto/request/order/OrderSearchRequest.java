package com.tanduydev.ecommerce.dto.request.order;

import com.tanduydev.ecommerce.enums.OrderStatus;
import com.tanduydev.ecommerce.enums.PaymentMethod;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSearchRequest {
    private String orderCode;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
}