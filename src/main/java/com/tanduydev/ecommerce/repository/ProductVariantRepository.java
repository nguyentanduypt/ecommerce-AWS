package com.tanduydev.ecommerce.repository;

import com.tanduydev.ecommerce.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    boolean existsBySku(String sku);
    Optional<ProductVariant> findBySku(String sku); // Rất hữu ích khi làm module Đơn hàng (Order)
}