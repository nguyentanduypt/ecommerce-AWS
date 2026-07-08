package com.tanduydev.ecommerce.repository;

import com.tanduydev.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findAllByCustomer_EmailOrderByCreatedAtDesc(String email);

    boolean existsByOrderCode(String orderCode);
}