package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.request.CategoryRequest;
import com.tanduydev.ecommerce.dto.response.CategoryResponse;
import com.tanduydev.ecommerce.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest request);
    CategoryResponse toResponse(Category category);
    List<CategoryResponse> toResponseList(List<Category> categories);
    void updateEntity(@MappingTarget Category category, CategoryRequest request);
}