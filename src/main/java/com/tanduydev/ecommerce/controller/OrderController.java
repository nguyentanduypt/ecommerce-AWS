package com.tanduydev.ecommerce.controller;

import com.tanduydev.ecommerce.dto.ApiResponse;
import com.tanduydev.ecommerce.dto.request.order.OrderRequest;
import com.tanduydev.ecommerce.dto.request.order.OrderSearchRequest;
import com.tanduydev.ecommerce.dto.response.order.OrderResponse;
import com.tanduydev.ecommerce.enums.OrderStatus;
import com.tanduydev.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            Principal principal,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Order created successfully",
                orderService.createOrder(principal.getName(), request)
        ));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched order history successfully",
                orderService.getMyOrders(principal.getName())
        ));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my-orders/{orderCode}")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrderDetails(
            Principal principal,
            @PathVariable String orderCode) {
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched order details successfully",
                orderService.getOrderByCode(principal.getName(), orderCode)
        ));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Order status updated successfully",
                orderService.updateOrderStatus(id, status)
        ));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @ModelAttribute OrderSearchRequest searchRequest,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        var result = orderService.getAllOrders(searchRequest, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                "Fetched all orders successfully",
                result.getData(),
                result.getPagination()
        ));
    }

}