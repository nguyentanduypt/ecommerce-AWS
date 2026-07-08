package com.tanduydev.ecommerce.dto.response.product;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductImageResponse {
    private UUID id;
    private String imageUrl;
}
