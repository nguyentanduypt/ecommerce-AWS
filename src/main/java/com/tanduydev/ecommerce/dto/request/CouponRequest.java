package com.tanduydev.ecommerce.dto.request;

import com.tanduydev.ecommerce.enums.CouponStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponRequest {
    @NotBlank(message = "Coupon code is required")
    @Size(min = 3, max = 50, message = "Coupon code must be between 3 and 50 characters")
    private String code;

    @Min(value = 0, message = "Discount percent cannot be negative")
    @Max(value = 100, message = "Discount percent cannot exceed 100")
    private Integer discountPercent;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount cannot be negative")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum order value cannot be negative")
    private BigDecimal minOrderValue;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Status is required")
    private CouponStatus status;
}