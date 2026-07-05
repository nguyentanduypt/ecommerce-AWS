package com.tanduydev.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@SQLDelete(sql = "UPDATE product_variants SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class ProductVariant extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(unique = true)
    private String sku;

    private String attributesCombination; // VD: "Color: Red, Size: XL"
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String status;
}