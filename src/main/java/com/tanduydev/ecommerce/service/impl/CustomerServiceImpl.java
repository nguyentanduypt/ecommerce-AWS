package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.CustomerRequest;
import com.tanduydev.ecommerce.dto.request.CustomerSearchRequest;
import com.tanduydev.ecommerce.dto.response.CustomerResponse;
import com.tanduydev.ecommerce.mapper.CustomerMapper;
import com.tanduydev.ecommerce.model.Customer;
import com.tanduydev.ecommerce.repository.CustomerRepository;
import com.tanduydev.ecommerce.repository.specification.CustomerSpecification;
import com.tanduydev.ecommerce.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<CustomerResponse> getAllCustomers(CustomerSearchRequest filter, Pageable pageable) {
        // Không dùng cache cho danh sách này vì Admin cần xem data realtime
        log.info("[CUSTOMER] Fetching all customers from DB");
        Page<Customer> customersPage = customerRepository.findAll(CustomerSpecification.withFilter(filter), pageable);
        return customersPage.map(customerMapper::toResponse);
    }

    @Override
    public CustomerResponse getCustomerById(UUID id) {
        String cacheKey = "customer:profile:" + id;

        // 1. Check Redis
        CustomerResponse cachedProfile = (CustomerResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProfile != null) {
            log.info("[CUSTOMER] Cache HIT for profile: {}", id);
            return cachedProfile;
        }

        // 2. Query DB
        log.info("[CUSTOMER] Cache MISS. Fetching profile from DB: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        CustomerResponse response = customerMapper.toResponse(customer);

        // 3. Save to Redis (Cache profile trong 1 giờ)
        redisTemplate.opsForValue().set(cacheKey, response, Duration.ofHours(1));

        return response;
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (!customer.getEmail().equals(request.getEmail()) && customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        customerMapper.updateEntity(customer, request);
        Customer savedCustomer = customerRepository.save(customer);

        // Xóa cache của profile này để lần gọi tiếp theo sẽ lấy data mới
        clearCustomerProfileCache(id);

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customerRepository.delete(customer);
        clearCustomerProfileCache(id);

        log.info("[CUSTOMER] Deleted customer with ID: {}", id);
    }

    // Hàm xóa cache profile
    private void clearCustomerProfileCache(UUID id) {
        String cacheKey = "customer:profile:" + id;
        redisTemplate.delete(cacheKey);
        log.info("[CUSTOMER] Cache cleared for profile: {}", id);
    }
}