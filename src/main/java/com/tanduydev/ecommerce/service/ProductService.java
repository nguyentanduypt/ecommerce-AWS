package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.product.ProductRequest;
import com.tanduydev.ecommerce.dto.request.product.ProductSearchRequest;
import com.tanduydev.ecommerce.dto.response.PagedResult;
import com.tanduydev.ecommerce.dto.response.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request, List<MultipartFile> images);
    PagedResult<ProductResponse> getAllProducts(ProductSearchRequest filter, Pageable pageable);
    ProductResponse getProductById(UUID id);
    ProductResponse updateProduct(UUID id, ProductRequest request, List<MultipartFile> images);
    void deleteProduct(UUID id);
}