package com.tanduydev.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class SoftDeleteBaseEntity extends BaseEntity {
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}