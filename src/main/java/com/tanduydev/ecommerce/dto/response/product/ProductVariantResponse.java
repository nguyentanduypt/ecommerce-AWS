package com.tanduydev.ecommerce.dto.response.product;

import com.tanduydev.ecommerce.enums.VariantStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductVariantResponse {
    private UUID id;
    private String sku;
    private String attributesCombination;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private VariantStatus status;
}