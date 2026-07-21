package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.CustomerRequest;
import com.tanduydev.ecommerce.dto.request.CustomerSearchRequest;
import com.tanduydev.ecommerce.dto.request.CustomerStatusUpdateRequest;
import com.tanduydev.ecommerce.dto.request.ProfileUpdateRequest;
import com.tanduydev.ecommerce.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    Page<CustomerResponse> getAllCustomers(CustomerSearchRequest filter, Pageable pageable);
    CustomerResponse getCustomerById(UUID id);
    CustomerResponse updateCustomerStatus(UUID id, CustomerStatusUpdateRequest request);
    CustomerResponse updateMyProfile(String email, ProfileUpdateRequest request, MultipartFile avatar);
    void deleteCustomer(UUID id);
}