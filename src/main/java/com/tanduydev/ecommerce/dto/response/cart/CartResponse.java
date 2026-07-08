package com.tanduydev.ecommerce.dto.response.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CartResponse {
    private UUID id;
    private List<CartItemResponse> items;
    private BigDecimal cartTotal;
}
