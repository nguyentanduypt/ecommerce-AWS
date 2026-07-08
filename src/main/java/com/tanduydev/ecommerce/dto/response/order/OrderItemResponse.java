package com.tanduydev.ecommerce.dto.response.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemResponse {
    private UUID id;
    private UUID variantId;
    private String sku;
    private String attributesCombination;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}