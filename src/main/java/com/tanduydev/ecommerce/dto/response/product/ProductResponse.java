package com.tanduydev.ecommerce.dto.response.product;

import com.tanduydev.ecommerce.enums.ProductStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;

    private UUID categoryId;
    private String categoryName;

    private UUID brandId;
    private String brandName;

    private ProductStatus productStatus;

    private List<ProductVariantResponse> variants;
    private List<ProductImageResponse> images;
}