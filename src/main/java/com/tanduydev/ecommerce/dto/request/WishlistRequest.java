package com.tanduydev.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class WishlistRequest {
    @NotNull(message = "Product ID is required")
    private UUID productId;
}