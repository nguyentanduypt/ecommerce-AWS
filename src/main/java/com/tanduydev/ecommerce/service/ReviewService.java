package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.ReviewRequest;
import com.tanduydev.ecommerce.dto.request.ReviewUpdateRequest;
import com.tanduydev.ecommerce.dto.response.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(String email, ReviewRequest request);
    List<ReviewResponse> getProductReviews(UUID productId);
    ReviewResponse updateReview(String email, UUID reviewId, ReviewUpdateRequest request);
    void deleteReview(String email, UUID reviewId);
}
