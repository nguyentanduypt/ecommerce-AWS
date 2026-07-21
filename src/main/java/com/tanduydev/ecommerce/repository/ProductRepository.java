package com.tanduydev.ecommerce.repository;

import com.tanduydev.ecommerce.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    Optional<Product> findBySlug(String slug);
    @EntityGraph(attributePaths = {"reviews", "variants"})
    Optional<Product> findById(UUID id);
}