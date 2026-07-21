package com.tanduydev.ecommerce.dto.response;

import com.tanduydev.ecommerce.enums.BrandStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class BrandResponse {
    private UUID id;
    private String name;
    private String imageUrl;
    private BrandStatus status;
}