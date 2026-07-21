package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.BrandRequest;
import com.tanduydev.ecommerce.dto.response.BrandResponse;
import com.tanduydev.ecommerce.mapper.BrandMapper;
import com.tanduydev.ecommerce.model.Brand;
import com.tanduydev.ecommerce.repository.BrandRepository;
import com.tanduydev.ecommerce.service.BrandService;
import com.tanduydev.ecommerce.service.BaseCacheService;
import com.tanduydev.ecommerce.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final BaseCacheService cacheService;
    private final FileService fileService;
    private static final String CACHE_KEY_ALL = "brands:all";

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request, MultipartFile image) {
        if (brandRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Brand name already exists");
        }

        Brand brand = brandMapper.toEntity(request);

        // Xử lý upload ảnh
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.uploadFile(image, "brands");
            brand.setImageUrl(imageUrl); // Đảm bảo Entity Brand của bạn đã có trường này
        }

        Brand savedBrand = brandRepository.save(brand);
        cacheService.evict(CACHE_KEY_ALL);
        return brandMapper.toResponse(savedBrand);
    }
    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        List cachedBrands = cacheService.get(CACHE_KEY_ALL, List.class);
        if (cachedBrands != null) {
            log.info("[BRAND] Cache HIT.");
            return cachedBrands;
        }

        log.info("[BRAND] Cache MISS. Fetching from DB...");
        List<Brand> brands = brandRepository.findAll();
        List<BrandResponse> responses = brandMapper.toResponseList(brands);

        cacheService.put(CACHE_KEY_ALL, responses, 30, TimeUnit.MINUTES);

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(UUID id, BrandRequest request, MultipartFile image) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        if (!brand.getName().equals(request.getName()) && brandRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Brand name already exists");
        }
        brandMapper.updateEntity(brand, request);
        if (image != null && !image.isEmpty()) {
            String newImageUrl = fileService.uploadFile(image, "brands");
            brand.setImageUrl(newImageUrl);
        }
        Brand updatedBrand = brandRepository.save(brand);
        cacheService.evict(CACHE_KEY_ALL);
        return brandMapper.toResponse(updatedBrand);
    }

    @Override
    @Transactional
    public void deleteBrand(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brandRepository.delete(brand);

        cacheService.evict(CACHE_KEY_ALL);

        log.info("[BRAND] Deleted brand with ID: {}", id);
    }
}