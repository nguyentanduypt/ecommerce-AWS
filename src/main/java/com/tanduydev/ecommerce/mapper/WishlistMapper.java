package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.response.WishlistResponse;
import com.tanduydev.ecommerce.model.Wishlist;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.slug", target = "productSlug")
    WishlistResponse toResponse(Wishlist wishlist);

    List<WishlistResponse> toResponseList(List<Wishlist> wishlists);

    @AfterMapping
    default void mapProductImage(@MappingTarget WishlistResponse response, Wishlist wishlist) {
        if (wishlist.getProduct() != null
                && wishlist.getProduct().getImages() != null
                && !wishlist.getProduct().getImages().isEmpty()) {
            response.setProductImage(wishlist.getProduct().getImages().get(0).getImageUrl());
        }
    }
}