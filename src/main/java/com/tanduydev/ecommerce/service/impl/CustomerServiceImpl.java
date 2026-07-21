package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.CustomerRequest;
import com.tanduydev.ecommerce.dto.request.CustomerSearchRequest;
import com.tanduydev.ecommerce.dto.request.CustomerStatusUpdateRequest;
import com.tanduydev.ecommerce.dto.request.ProfileUpdateRequest;
import com.tanduydev.ecommerce.dto.response.CustomerResponse;
import com.tanduydev.ecommerce.enums.UserStatus;
import com.tanduydev.ecommerce.mapper.CustomerMapper;
import com.tanduydev.ecommerce.model.Customer;
import com.tanduydev.ecommerce.repository.CustomerRepository;
import com.tanduydev.ecommerce.repository.RoleRepository;
import com.tanduydev.ecommerce.repository.specification.CustomerSpecification;
import com.tanduydev.ecommerce.service.CustomerService;
import com.tanduydev.ecommerce.service.FileService;
import com.tanduydev.ecommerce.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("[ADMIN] Creating new customer: {}", request.getEmail());

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setGenderEnum(request.getGenderEnum());
        customer.setStatus(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            customer.setPassword(passwordEncoder.encode("123456aA@"));
        }

        customer.setRole(roleRepository.findByName(AppConstants.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Default role not found")));

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(CustomerSearchRequest filter, Pageable pageable) {
        log.info("[CUSTOMER] Fetching all customers from DB");
        Page<Customer> customersPage = customerRepository.findAll(CustomerSpecification.withFilter(filter), pageable);
        return customersPage.map(customerMapper::toResponse);
    }

    @Override
    public CustomerResponse getCustomerById(UUID id) {
        String cacheKey = "customer:profile:" + id;

        CustomerResponse cachedProfile = (CustomerResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProfile != null) {
            log.info("[CUSTOMER] Cache HIT for profile: {}", id);
            return cachedProfile;
        }
        log.info("[CUSTOMER] Cache MISS. Fetching profile from DB: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        CustomerResponse response = customerMapper.toResponse(customer);
        redisTemplate.opsForValue().set(cacheKey, response, Duration.ofHours(1));
        return response;
    }

    @Override
    @Transactional
    public CustomerResponse updateMyProfile(String email, ProfileUpdateRequest request, MultipartFile avatar) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Cập nhật thông tin text thông qua Mapper
        customerMapper.updateEntity(customer, request);

        // Xử lý upload avatar nếu client gửi file
        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = fileService.uploadFile(avatar, "avatars");
            customer.setAvatarUrl(avatarUrl);
        }

        Customer savedCustomer = customerRepository.save(customer);
        clearCustomerProfileCache(savedCustomer.getId());
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomerStatus(UUID id, CustomerStatusUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customer.setStatus(request.getStatus());

        Customer savedCustomer = customerRepository.save(customer);
        clearCustomerProfileCache(savedCustomer.getId());

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Tùy chọn: Nếu muốn xóa luôn ảnh trên S3 khi xóa user thì thêm đoạn này
        if (customer.getAvatarUrl() != null) {
            fileService.deleteFile(customer.getAvatarUrl());
        }

        customerRepository.delete(customer);
        clearCustomerProfileCache(id);
        log.info("[CUSTOMER] Deleted customer with ID: {}", id);
    }

    private void clearCustomerProfileCache(UUID id) {
        String cacheKey = "customer:profile:" + id;
        try {
            redisTemplate.delete(cacheKey);
            log.info("[CUSTOMER] Cache cleared for profile: {}", id);
        } catch (Exception e) {
            log.warn("[CUSTOMER] Redis is down! Failed to clear cache for profile {}. Error: {}", id, e.getMessage());
        }
    }
}