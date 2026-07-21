package com.tanduydev.ecommerce.dto.request.product;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductVariantRequest {
    @JsonProperty("variantId")
    @JsonAlias({"id", "variantId"})
    private String id;
    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Attributes combination is required (e.g., Color: Red, Size: XL)")
    private String attributesCombination;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be > 0")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private String imageUrl;
    private String status;
}
