package com.tanduydev.ecommerce.repository;

import com.tanduydev.ecommerce.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findAllByCustomer_Email(String email);
    boolean existsByCustomer_EmailAndProduct_Id(String email, UUID productId);
    void deleteByCustomer_EmailAndProduct_Id(String email, UUID productId);
}
