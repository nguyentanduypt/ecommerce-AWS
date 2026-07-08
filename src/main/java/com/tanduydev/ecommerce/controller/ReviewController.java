package com.tanduydev.ecommerce.controller;

import com.tanduydev.ecommerce.dto.ApiResponse;
import com.tanduydev.ecommerce.dto.request.ReviewRequest;
import com.tanduydev.ecommerce.dto.request.ReviewUpdateRequest;
import com.tanduydev.ecommerce.dto.response.ReviewResponse;
import com.tanduydev.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {


    private final ReviewService reviewService;

    // Public API: Ai cũng xem được đánh giá của sản phẩm
    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviews(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success("Fetched reviews successfully",
                reviewService.getProductReviews(productId)));
    }

    // Customer API: Chỉ khách hàng đăng nhập mới được đánh giá
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(Principal principal, @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Review created successfully",
                reviewService.createReview(principal.getName(), request)));
    }

    // Customer API: Khách hàng tự xóa đánh giá của mình
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(Principal principal, @PathVariable UUID id) {
        reviewService.deleteReview(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }


    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody ReviewUpdateRequest request) {

        return ResponseEntity.ok(ApiResponse.success("Review updated successfully",
                reviewService.updateReview(principal.getName(), id, request)));
    }
}
