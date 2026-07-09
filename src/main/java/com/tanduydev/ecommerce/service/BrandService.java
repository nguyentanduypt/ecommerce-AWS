package com.tanduydev.ecommerce.service;


import com.tanduydev.ecommerce.dto.request.BrandRequest;
import com.tanduydev.ecommerce.dto.response.BrandResponse;

import java.util.List;
import java.util.UUID;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    List<BrandResponse> getAllBrands();
    BrandResponse getBrandById(UUID id);
    BrandResponse updateBrand(UUID id, BrandRequest request);
    void deleteBrand(UUID id);
}