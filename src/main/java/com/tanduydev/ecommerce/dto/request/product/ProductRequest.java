package com.tanduydev.ecommerce.dto.request.product;

import com.tanduydev.ecommerce.enums.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotNull(message = "Brand ID is required")
    private UUID brandId;

    @NotNull(message = "Product status is required")
    private ProductStatus productStatus;

    @Valid
    @NotEmpty(message = "Product must have at least one variant")
    private List<ProductVariantRequest> variants;

    @Valid
    private List<ProductImageRequest> images;
}