package com.tanduydev.ecommerce.model;

import com.tanduydev.ecommerce.enums.VariantStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
// THÊM DÒNG NÀY: Bỏ qua các trường khác, chỉ so sánh bằng trường được Include
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@SQLDelete(sql = "UPDATE product_variants SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class ProductVariant extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @EqualsAndHashCode.Include
    @Column(unique = true)
    private String sku;

    private String attributesCombination;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be > 0")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VariantStatus status;
}