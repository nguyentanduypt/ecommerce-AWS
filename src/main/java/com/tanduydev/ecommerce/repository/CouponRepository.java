package com.tanduydev.ecommerce.repository;

import com.tanduydev.ecommerce.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    boolean existsByCode(String code);
    Optional<Coupon> findByCode(String code);
}