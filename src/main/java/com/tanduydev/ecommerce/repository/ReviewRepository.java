package com.tanduydev.ecommerce.repository;

import com.tanduydev.ecommerce.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findAllByProduct_Id(UUID productId);
    boolean existsByCustomer_EmailAndProduct_Id(String email, UUID productId);
}