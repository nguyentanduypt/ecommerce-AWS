package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.response.order.OrderItemResponse;
import com.tanduydev.ecommerce.dto.response.order.OrderResponse;
import com.tanduydev.ecommerce.model.Order;
import com.tanduydev.ecommerce.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponseList(List<Order> orders);

    @Mapping(source = "productVariant.id", target = "variantId")
    OrderItemResponse toItemResponse(OrderItem orderItem);
}