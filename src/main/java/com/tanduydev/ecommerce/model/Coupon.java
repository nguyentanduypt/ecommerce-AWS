package com.tanduydev.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@SQLDelete(sql = "UPDATE coupons SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Coupon extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String code;
    private Integer discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal minOrderValue;
    private LocalDateTime expiryDate;
    private Integer quantity;
    private String status;
}