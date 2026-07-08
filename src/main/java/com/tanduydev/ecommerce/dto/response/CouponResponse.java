package com.tanduydev.ecommerce.dto.response;

import com.tanduydev.ecommerce.enums.CouponStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CouponResponse {
    private UUID id;
    private String code;
    private Integer discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal minOrderValue;
    private LocalDateTime expiryDate;
    private Integer quantity;
    private CouponStatus status;
}