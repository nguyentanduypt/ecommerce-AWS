package com.tanduydev.ecommerce.service;


import com.tanduydev.ecommerce.dto.request.BrandRequest;
import com.tanduydev.ecommerce.dto.response.BrandResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request, MultipartFile image);
    List<BrandResponse> getAllBrands();
    BrandResponse getBrandById(UUID id);
    BrandResponse updateBrand(UUID id, BrandRequest request, MultipartFile image);
    void deleteBrand(UUID id);
}