package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.CategoryRequest;
import com.tanduydev.ecommerce.dto.response.CategoryResponse;
import com.tanduydev.ecommerce.mapper.CategoryMapper;
import com.tanduydev.ecommerce.model.Category;
import com.tanduydev.ecommerce.repository.CategoryRepository;
import com.tanduydev.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("[CATEGORY] Creating new category: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);

        log.info("[CATEGORY] Successfully created category with ID: {}", category.getId());
        return categoryMapper.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }

    @Override
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
        categoryRepository.save(category);

        log.info("[CATEGORY] Successfully updated category with ID: {}", id);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(category);
        log.info("[CATEGORY] Deleted category with ID: {}", id);
    }
}