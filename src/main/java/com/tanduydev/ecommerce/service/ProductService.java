package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.product.ProductRequest;
import com.tanduydev.ecommerce.dto.response.product.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(UUID id);
    ProductResponse updateProduct(UUID id, ProductRequest request);
    void deleteProduct(UUID id);
}