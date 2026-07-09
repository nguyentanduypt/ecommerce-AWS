package com.tanduydev.ecommerce.repository;

import com.tanduydev.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findAllByCustomer_Email(String email);
    Optional<Address> findByIdAndCustomer_Email(UUID id, String email);
    List<Address> findAllByCustomer_EmailAndIsDefaultTrue(String email);
}