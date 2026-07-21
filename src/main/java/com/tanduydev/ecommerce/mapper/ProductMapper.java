package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.request.product.ProductRequest;
import com.tanduydev.ecommerce.dto.response.product.ProductResponse;
import com.tanduydev.ecommerce.model.Product;
import com.tanduydev.ecommerce.model.ProductVariant;
import com.tanduydev.ecommerce.model.Review;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
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

    @AfterMapping
    default void linkRelations(@MappingTarget Product product) {
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> variant.setProduct(product));
        }
        if (product.getImages() != null) {
            product.getImages().forEach(image -> image.setProduct(product));
        }
    }

    @AfterMapping
    default void calculateProductStats(@MappingTarget ProductResponse response, Product product) {
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            double averageRating = product.getReviews().stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            response.setRating(Math.round(averageRating * 10.0) / 10.0);
            response.setReviewCount(product.getReviews().size());
        } else {
            response.setRating(0.0);
            response.setReviewCount(0);
        }

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            int totalStock = product.getVariants().stream().mapToInt(ProductVariant::getStock).sum();
            response.setStock(totalStock);
            BigDecimal minPrice = product.getVariants().stream()
                    .map(ProductVariant::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            response.setPrice(minPrice);
        } else {
            response.setStock(0);
            response.setPrice(BigDecimal.ZERO);
        }
    }
}