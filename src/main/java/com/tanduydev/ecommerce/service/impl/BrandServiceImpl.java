package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.BrandRequest;
import com.tanduydev.ecommerce.dto.response.BrandResponse;
import com.tanduydev.ecommerce.mapper.BrandMapper;
import com.tanduydev.ecommerce.model.Brand;
import com.tanduydev.ecommerce.repository.BrandRepository;
import com.tanduydev.ecommerce.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        log.info("[BRAND] Creating new brand: {}", request.getName());

        if (brandRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Brand name already exists");
        }

        Brand brand = brandMapper.toEntity(request);
        brandRepository.save(brand);

        log.info("[BRAND] Successfully created brand with ID: {}", brand.getId());
        return brandMapper.toResponse(brand);
    }

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandMapper.toResponseList(brandRepository.findAll());
    }

    @Override
    public BrandResponse getBrandById(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(UUID id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        if (!brand.getName().equals(request.getName()) && brandRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Brand name already exists");
        }

        brandMapper.updateEntity(brand, request);
        brandRepository.save(brand);

        log.info("[BRAND] Successfully updated brand with ID: {}", id);
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(UUID id) {
        // Nhờ có @SQLDelete ở Entity, hàm deleteById sẽ tự động chạy câu UPDATE deleted_at
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brandRepository.delete(brand);
        log.info("[BRAND] Soft deleted brand with ID: {}", id);
    }
}