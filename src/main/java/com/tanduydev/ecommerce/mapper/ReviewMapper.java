package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.request.ReviewRequest;
import com.tanduydev.ecommerce.dto.request.ReviewUpdateRequest;
import com.tanduydev.ecommerce.dto.response.ReviewResponse;
import com.tanduydev.ecommerce.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Review toEntity(ReviewRequest request);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "customer.fullName", target = "customerName")
    ReviewResponse toResponse(Review review);
    void updateEntity(@MappingTarget Review review, ReviewUpdateRequest request);
    List<ReviewResponse> toResponseList(List<Review> reviews);
}