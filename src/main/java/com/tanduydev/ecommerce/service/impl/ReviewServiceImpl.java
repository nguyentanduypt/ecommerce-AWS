package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.ReviewRequest;
import com.tanduydev.ecommerce.dto.request.ReviewUpdateRequest;
import com.tanduydev.ecommerce.dto.response.ReviewResponse;
import com.tanduydev.ecommerce.mapper.ReviewMapper;
import com.tanduydev.ecommerce.model.Customer;
import com.tanduydev.ecommerce.model.Product;
import com.tanduydev.ecommerce.model.Review;
import com.tanduydev.ecommerce.repository.ProductRepository;
import com.tanduydev.ecommerce.repository.ReviewRepository;
import com.tanduydev.ecommerce.repository.UserRepository;
import com.tanduydev.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse createReview(String email, ReviewRequest request) {
        log.info("[REVIEW] User {} creating review for product {}", email, request.getProductId());

        if (reviewRepository.existsByCustomer_EmailAndProduct_Id(email, request.getProductId())) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }

        Customer customer = (Customer) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = reviewMapper.toEntity(request);
        review.setCustomer(customer);
        review.setProduct(product);

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> getProductReviews(UUID productId) {
        return reviewMapper.toResponseList(reviewRepository.findAllByProduct_Id(productId));
    }

    @Override
    @Transactional
    public void deleteReview(String email, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Chỉ cho phép người tạo ra review đó được quyền xóa
        if (!review.getCustomer().getEmail().equals(email)) {
            throw new RuntimeException("You do not have permission to delete this review");
        }

        reviewRepository.delete(review);
        log.info("[REVIEW] Review {} deleted by user {}", reviewId, email);
    }
    @Override
    @Transactional
    public ReviewResponse updateReview(String email, UUID reviewId, ReviewUpdateRequest request) {
        // 1. Tìm review trong Database
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // 2. Kiểm tra xem người đang request có phải là chủ nhân của review này không
        if (!review.getCustomer().getEmail().equals(email)) {
            throw new RuntimeException("You do not have permission to update this review");
        }

        // 3. Cập nhật dữ liệu mới và lưu lại
        reviewMapper.updateEntity(review, request);

        log.info("[REVIEW] Review {} updated by user {}", reviewId, email);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }
}
