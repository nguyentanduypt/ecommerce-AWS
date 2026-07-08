package com.tanduydev.ecommerce.controller;

import com.tanduydev.ecommerce.dto.ApiResponse;
import com.tanduydev.ecommerce.dto.request.BrandRequest;
import com.tanduydev.ecommerce.dto.response.BrandResponse;
import com.tanduydev.ecommerce.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        return ResponseEntity.ok(ApiResponse.success("Fetched brands successfully", brandService.getAllBrands()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Fetched brand successfully", brandService.getBrandById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Brand created successfully", brandService.createBrand(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(@PathVariable UUID id, @Valid @RequestBody BrandRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Brand updated successfully", brandService.updateBrand(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully", null));
    }
}