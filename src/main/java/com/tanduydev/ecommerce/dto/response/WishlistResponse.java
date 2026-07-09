package com.tanduydev.ecommerce.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class WishlistResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productSlug;
}