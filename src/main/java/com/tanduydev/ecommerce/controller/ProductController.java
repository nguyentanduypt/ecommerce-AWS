package com.tanduydev.ecommerce.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanduydev.ecommerce.dto.ApiResponse;
import com.tanduydev.ecommerce.dto.request.product.ProductRequest;
import com.tanduydev.ecommerce.dto.request.product.ProductSearchRequest;
import com.tanduydev.ecommerce.dto.response.product.ProductResponse;
import com.tanduydev.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @ModelAttribute ProductSearchRequest searchRequest,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        var result = productService.getAllProducts(searchRequest, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                "Fetched products successfully",
                result.getData(),
                result.getPagination()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Fetched product successfully", productService.getProductById(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {

        // Parse JSON String sang Object
        ObjectMapper mapper = new ObjectMapper();
        ProductRequest request = mapper.readValue(productJson, ProductRequest.class);

        return ResponseEntity.ok(ApiResponse.success(
                "Product created",
                productService.createProduct(request, images)
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @RequestPart("product") String productJson, // ĐÃ SỬA: Dùng String JSON y hệt hàm Create
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ProductRequest request = mapper.readValue(productJson, ProductRequest.class);

        return ResponseEntity.ok(ApiResponse.success("Product updated successfully",
                productService.updateProduct(id, request, images)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}