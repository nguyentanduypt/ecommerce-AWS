package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.order.OrderRequest;
import com.tanduydev.ecommerce.dto.response.order.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(String email, OrderRequest request);
    List<OrderResponse> getMyOrders(String email);
    OrderResponse getOrderByCode(String email, String orderCode);
}
