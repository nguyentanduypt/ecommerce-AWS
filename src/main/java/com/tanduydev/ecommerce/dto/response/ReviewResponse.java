package com.tanduydev.ecommerce.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class ReviewResponse {
    private UUID id;
    private UUID productId;
    private String customerName;
    private Integer rating;
    private String comment;
    private String avatarUrl;
}