package com.tanduydev.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @Column(nullable = false, length = 100)
    private String receiverName;
    @Column(nullable = false, length = 15)
    private String receiverPhone;
//    private String provinceCity;
//    private String district;
//    private String ward;
//    private String detailAddress;
@Column(nullable = false, columnDefinition = "TEXT")
    private String detailAddress;
    private Boolean isDefault;
}