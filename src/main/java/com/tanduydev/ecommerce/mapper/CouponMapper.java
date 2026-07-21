package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.request.CouponRequest;
import com.tanduydev.ecommerce.dto.response.CouponResponse;
import com.tanduydev.ecommerce.model.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    @Mapping(target = "expiryDate", expression = "java(request.getExpiryDate() != null ? request.getExpiryDate().atTime(java.time.LocalTime.MAX) : null)")
    Coupon toEntity(CouponRequest request);
    CouponResponse toResponse(Coupon coupon);
    List<CouponResponse> toResponseList(List<Coupon> coupons);
    @Mapping(target = "expiryDate", expression = "java(request.getExpiryDate() != null ? request.getExpiryDate().atTime(java.time.LocalTime.MAX) : null)")
    void updateEntity(@MappingTarget Coupon coupon, CouponRequest request);
}