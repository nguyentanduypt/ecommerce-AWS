package com.tanduydev.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "brands")
@Getter
@Setter
@SQLDelete(sql = "UPDATE brands SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Brand extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String slug;
}