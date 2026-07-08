package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.CouponRequest;
import com.tanduydev.ecommerce.dto.response.CouponResponse;
import com.tanduydev.ecommerce.mapper.CouponMapper;
import com.tanduydev.ecommerce.model.Coupon;
import com.tanduydev.ecommerce.repository.CouponRepository;
import com.tanduydev.ecommerce.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        log.info("[COUPON] Creating new coupon with code: {}", request.getCode());

        if (couponRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists");
        }

        // Logic check: Không được vừa giảm theo phần trăm vừa giảm theo số tiền (Nên chọn 1 trong 2)
        if (request.getDiscountPercent() != null && request.getDiscountAmount() != null) {
            throw new IllegalArgumentException("Cannot set both discount percent and discount amount");
        }

        Coupon coupon = couponMapper.toEntity(request);
        coupon.setCode(request.getCode().toUpperCase());

        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponMapper.toResponseList(couponRepository.findAll());
    }

    @Override
    public CouponResponse getCouponById(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        return couponMapper.toResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(UUID id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        if (!coupon.getCode().equalsIgnoreCase(request.getCode()) && couponRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists");
        }

        if (request.getDiscountPercent() != null && request.getDiscountAmount() != null) {
            throw new IllegalArgumentException("Cannot set both discount percent and discount amount");
        }

        couponMapper.updateEntity(coupon, request);
        coupon.setCode(request.getCode().toUpperCase());

        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public void deleteCoupon(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        couponRepository.delete(coupon); // Kích hoạt @SQLDelete
        log.info("[COUPON] Soft deleted coupon with ID: {}", id);
    }
}