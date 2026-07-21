package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.order.OrderRequest;
import com.tanduydev.ecommerce.dto.request.order.OrderSearchRequest;
import com.tanduydev.ecommerce.dto.response.PagedResult;
import com.tanduydev.ecommerce.dto.response.order.OrderResponse;

import java.util.List;
import java.util.UUID;

import com.tanduydev.ecommerce.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(String email, OrderRequest request);
    List<OrderResponse> getMyOrders(String email);
    OrderResponse getOrderByCode(String email, String orderCode);
    OrderResponse updateOrderStatus(UUID id, OrderStatus newStatus);
    PagedResult<OrderResponse> getAllOrders(OrderSearchRequest filter, Pageable pageable);
}