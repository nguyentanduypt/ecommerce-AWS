package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.product.ProductRequest;
import com.tanduydev.ecommerce.dto.request.product.ProductVariantRequest;
import com.tanduydev.ecommerce.dto.response.product.ProductResponse;
import com.tanduydev.ecommerce.mapper.ProductMapper;
import com.tanduydev.ecommerce.model.Brand;
import com.tanduydev.ecommerce.model.Category;
import com.tanduydev.ecommerce.model.Product;
import com.tanduydev.ecommerce.repository.BrandRepository;
import com.tanduydev.ecommerce.repository.CategoryRepository;
import com.tanduydev.ecommerce.repository.ProductRepository;
import com.tanduydev.ecommerce.repository.ProductVariantRepository;
import com.tanduydev.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("[PRODUCT] Creating product: {}", request.getName());

        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists");
        }

        // Validate: Không được có SKU nào trùng lặp với CSDL
        if (request.getVariants() != null) {
            for (ProductVariantRequest v : request.getVariants()) {
                if (variantRepository.existsBySku(v.getSku())) {
                    throw new IllegalArgumentException("SKU already exists: " + v.getSku());
                }
            }
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setBrand(brand);

        // Sinh Slug tự động & Kiểm tra trùng lặp Slug
        String baseSlug = generateSlug(request.getName());
        String uniqueSlug = baseSlug;
        int count = 1;
        while (productRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + count++;
        }
        product.setSlug(uniqueSlug);

        // Lưu Product (Cascade sẽ tự lưu Variants và Images)
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productMapper.toResponseList(productRepository.findAll());
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        // Xóa sạch danh sách cũ để tránh rác DB khi dùng orphanRemoval = true
        if (product.getVariants() != null) product.getVariants().clear();
        if (product.getImages() != null) product.getImages().clear();

        productMapper.updateEntity(product, request);
        product.setCategory(category);
        product.setBrand(brand);

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productRepository.delete(product);
        log.info("[PRODUCT] Soft deleted product with ID: {}", id);
    }

    private String generateSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}