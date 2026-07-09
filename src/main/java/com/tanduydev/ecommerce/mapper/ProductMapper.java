package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.request.product.ProductRequest;
import com.tanduydev.ecommerce.dto.response.product.ProductResponse;
import com.tanduydev.ecommerce.model.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "brand.name", target = "brandName")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    void updateEntity(@MappingTarget Product product, ProductRequest request);

    // Tự động set Product cha vào các Entity con (Variants, Images) trước khi lưu DB
    @AfterMapping
    default void linkRelations(@MappingTarget Product product) {
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> variant.setProduct(product));
        }
        if (product.getImages() != null) {
            product.getImages().forEach(image -> image.setProduct(product));
        }
    }
}