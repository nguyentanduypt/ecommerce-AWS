package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.request.BrandRequest;
import com.tanduydev.ecommerce.dto.response.BrandResponse;
import com.tanduydev.ecommerce.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toEntity(BrandRequest request);
    BrandResponse toResponse(Brand brand);
    List<BrandResponse> toResponseList(List<Brand> brands);
    void updateEntity(@MappingTarget Brand brand, BrandRequest request);
}