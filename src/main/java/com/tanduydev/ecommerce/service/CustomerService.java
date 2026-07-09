package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.CustomerRequest;
import com.tanduydev.ecommerce.dto.request.CustomerSearchRequest;
import com.tanduydev.ecommerce.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {
    Page<CustomerResponse> getAllCustomers(CustomerSearchRequest filter, Pageable pageable);
    CustomerResponse getCustomerById(UUID id);
    CustomerResponse updateCustomer(UUID id, CustomerRequest request);
    void deleteCustomer(UUID id);
}