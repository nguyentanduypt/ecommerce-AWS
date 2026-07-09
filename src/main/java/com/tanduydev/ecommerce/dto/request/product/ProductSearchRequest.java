package com.tanduydev.ecommerce.dto.request.product;

import com.tanduydev.ecommerce.enums.ProductStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductSearchRequest {
    private String name;
    private UUID categoryId;
    private UUID brandId;
    private Double minPrice;
    private Double maxPrice;
    private ProductStatus status;
}