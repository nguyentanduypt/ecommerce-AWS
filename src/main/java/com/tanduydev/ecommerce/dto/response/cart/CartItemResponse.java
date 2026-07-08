package com.tanduydev.ecommerce.dto.response.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CartItemResponse {
    private UUID id;
    private UUID variantId;
    private String productName;
    private String attributesCombination;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;
}