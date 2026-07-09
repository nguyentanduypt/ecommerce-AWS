package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.CouponRequest;
import com.tanduydev.ecommerce.dto.response.CouponResponse;

import java.util.List;
import java.util.UUID;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);
    List<CouponResponse> getAllCoupons();
    CouponResponse getCouponById(UUID id);
    CouponResponse updateCoupon(UUID id, CouponRequest request);
    void deleteCoupon(UUID id);
}