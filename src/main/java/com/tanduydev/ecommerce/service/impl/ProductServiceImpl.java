package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.product.ProductImageRequest;
import com.tanduydev.ecommerce.dto.request.product.ProductRequest;
import com.tanduydev.ecommerce.dto.request.product.ProductSearchRequest;
import com.tanduydev.ecommerce.dto.request.product.ProductVariantRequest;
import com.tanduydev.ecommerce.dto.response.PagedResult;
import com.tanduydev.ecommerce.dto.response.product.ProductResponse;
import com.tanduydev.ecommerce.mapper.ProductMapper;
import com.tanduydev.ecommerce.model.Brand;
import com.tanduydev.ecommerce.model.Category;
import com.tanduydev.ecommerce.model.Product;
import com.tanduydev.ecommerce.model.ProductVariant;
import com.tanduydev.ecommerce.repository.BrandRepository;
import com.tanduydev.ecommerce.repository.CategoryRepository;
import com.tanduydev.ecommerce.repository.ProductRepository;
import com.tanduydev.ecommerce.repository.ProductVariantRepository;
import com.tanduydev.ecommerce.repository.specification.ProductSpecification;
import com.tanduydev.ecommerce.service.FileService;
import com.tanduydev.ecommerce.service.ProductService;
import com.tanduydev.ecommerce.service.BaseCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
    private final FileService fileService;
    private final BaseCacheService cacheService;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request, List<MultipartFile> images) {
        log.info("[PRODUCT] Creating product: {}", request.getName());

        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists");
        }

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

        // Khởi tạo list ảnh tránh lỗi NullPointerException
        if (product.getImages() == null) {
            product.setImages(new ArrayList<>());
        }

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imageUrl = fileService.uploadFile(image, "products");
                com.tanduydev.ecommerce.model.ProductImage newImg = new com.tanduydev.ecommerce.model.ProductImage();
                newImg.setImageUrl(imageUrl);
                newImg.setProduct(product);
                product.getImages().add(newImg);
            }
        }

        String baseSlug = generateSlug(request.getName());
        String uniqueSlug = baseSlug;
        int count = 1;
        while (productRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + count++;
        }
        product.setSlug(uniqueSlug);

        Product savedProduct = productRepository.save(product);

        cacheService.evictByPattern("products:search:*");

        return productMapper.toResponse(savedProduct);
    }
    @Override
    @Transactional(readOnly = true)
    public PagedResult<ProductResponse> getAllProducts(ProductSearchRequest filter, Pageable pageable) {
        String cacheKey = String.format("products:search:filter:%s:page:%d:size:%d:sort:%s",
                (filter != null ? filter.hashCode() : "all"),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString());

        // 1. Kiểm tra Cache
        PagedResult cachedResult = cacheService.get(cacheKey, PagedResult.class);
        if (cachedResult != null) {
            log.info("[PRODUCT] Cache HIT for key: {}", cacheKey);
            return cachedResult;
        }

        // 2. Query DB nếu Cache MISS
        log.info("[PRODUCT] Cache MISS for key: {}. Querying DB...", cacheKey);
        Page<Product> productsPage = productRepository.findAll(ProductSpecification.withFilter(filter), pageable);

        // 3. Chuyển đổi Page sang PagedResult
        PagedResult<ProductResponse> result = new PagedResult<>(productsPage.map(productMapper::toResponse));

        // 4. Lưu Cache (Ví dụ lưu 5 phút)
        cacheService.put(cacheKey, result, 5, TimeUnit.MINUTES);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request, List<MultipartFile> images) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setProductStatus(request.getProductStatus());
        product.setCategory(category);
        product.setBrand(brand);

        if (request.getVariants() != null) {
            List<String> requestVariantIds = request.getVariants().stream()
                    .map(ProductVariantRequest::getId)
                    .filter(vid -> vid != null && !vid.trim().isEmpty())
                    .toList();

            product.getVariants().removeIf(existingVariant ->
                    existingVariant.getId() != null && !requestVariantIds.contains(existingVariant.getId().toString())
            );

            for (ProductVariantRequest vReq : request.getVariants()) {
                if (vReq.getId() != null && !vReq.getId().trim().isEmpty()) {
                    product.getVariants().stream()
                            .filter(v -> v.getId().toString().equals(vReq.getId()))
                            .findFirst()
                            .ifPresent(existingVariant -> {
                                existingVariant.setSku(vReq.getSku());
                                existingVariant.setAttributesCombination(vReq.getAttributesCombination());
                                existingVariant.setPrice(vReq.getPrice());
                                existingVariant.setStock(vReq.getStock());
                                existingVariant.setStatus(com.tanduydev.ecommerce.enums.VariantStatus.valueOf(vReq.getStatus()));
                            });
                } else {
                    ProductVariant newVariant = new ProductVariant();
                    newVariant.setSku(vReq.getSku());
                    newVariant.setAttributesCombination(vReq.getAttributesCombination());
                    newVariant.setPrice(vReq.getPrice());
                    newVariant.setStock(vReq.getStock());
                    newVariant.setStatus(com.tanduydev.ecommerce.enums.VariantStatus.valueOf(vReq.getStatus()));
                    newVariant.setProduct(product);
                    product.getVariants().add(newVariant);
                }
            }
        }

        if (product.getImages() == null) {
            product.setImages(new ArrayList<>());
        }

        if (request.getImages() != null) {
            List<String> retainedUrls = request.getImages().stream()
                    .map(ProductImageRequest::getImageUrl)
                    .toList();
            product.getImages().removeIf(img -> !retainedUrls.contains(img.getImageUrl()));
        } else {
            product.getImages().clear();
        }

        // 2. Thêm các ảnh upload file mới vào
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imageUrl = fileService.uploadFile(image, "products");
                com.tanduydev.ecommerce.model.ProductImage newImg = new com.tanduydev.ecommerce.model.ProductImage();
                newImg.setImageUrl(imageUrl);
                newImg.setProduct(product);
                product.getImages().add(newImg);
            }
        }

        Product savedProduct = productRepository.save(product);
        cacheService.evictByPattern("products:search:*");
        return productMapper.toResponse(savedProduct);
    }
    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productRepository.delete(product);

        // Gọi cacheService
        cacheService.evictByPattern("products:search:*");

        log.info("[PRODUCT] Soft deleted product with ID: {}", id);
    }

    private String generateSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}