package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.CategoryRequest;
import com.tanduydev.ecommerce.dto.response.CategoryResponse;
import com.tanduydev.ecommerce.mapper.CategoryMapper;
import com.tanduydev.ecommerce.model.Category;
import com.tanduydev.ecommerce.repository.CategoryRepository;
import com.tanduydev.ecommerce.service.CategoryService;
import com.tanduydev.ecommerce.service.BaseCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BaseCacheService cacheService; // Sử dụng BaseCacheService

    private static final String CACHE_KEY_ALL = "categories:all";

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("[CATEGORY] Creating new category: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);

        // Xóa cache danh sách
        cacheService.evict(CACHE_KEY_ALL);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        // 1. Lấy từ Cache
        List cachedCategories = cacheService.get(CACHE_KEY_ALL, List.class);
        if (cachedCategories != null) {
            log.info("[CATEGORY] Cache HIT.");
            return cachedCategories;
        }

        // 2. Lấy từ DB nếu Cache MISS
        log.info("[CATEGORY] Cache MISS. Fetching from DB...");
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> responses = categoryMapper.toResponseList(categories);

        // 3. Lưu vào Cache (Ví dụ 30 phút)
        cacheService.put(CACHE_KEY_ALL, responses, 30, TimeUnit.MINUTES);

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        categoryMapper.updateEntity(category, request);
        Category updatedCategory = categoryRepository.save(category);

        // Xóa cache danh sách
        cacheService.evict(CACHE_KEY_ALL);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(category);

        // Xóa cache danh sách
        cacheService.evict(CACHE_KEY_ALL);

        log.info("[CATEGORY] Deleted category with ID: {}", id);
    }
}