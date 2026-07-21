package com.tanduydev.ecommerce.dto.request;

import com.tanduydev.ecommerce.enums.BrandStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {
    @NotBlank(message = "Brand name is required")
    private String name;
    private BrandStatus status;
    private String imageUrl;
}