package com.tanduydev.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private String transactionId;
    private String partnerCode;
    private String requestId;
    private Integer responseCode;
    private LocalDateTime paymentTime;
}