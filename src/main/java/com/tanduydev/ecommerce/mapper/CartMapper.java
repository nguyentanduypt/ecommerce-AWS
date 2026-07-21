package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.response.cart.CartItemResponse;
import com.tanduydev.ecommerce.dto.response.cart.CartResponse;
import com.tanduydev.ecommerce.model.Cart;
import com.tanduydev.ecommerce.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cartItems", target = "items")
    CartResponse toCartResponse(Cart cart);

    @Mapping(source = "productVariant.id", target = "variantId")
    @Mapping(source = "productVariant.product.name", target = "productName")
    @Mapping(source = "productVariant.product.id", target = "productId")
    @Mapping(source = "productVariant.attributesCombination", target = "attributesCombination")
    @Mapping(source = "productVariant.imageUrl", target = "imageUrl")
    @Mapping(source = "productVariant.price", target = "price")
    CartItemResponse toCartItemResponse(CartItem cartItem);
    @AfterMapping
    default void setFallbackImage(CartItem cartItem, @MappingTarget CartItemResponse response) {
        if (response.getImageUrl() == null || response.getImageUrl().isEmpty()) {
            if (cartItem.getProductVariant() != null && cartItem.getProductVariant().getProduct() != null) {
                var product = cartItem.getProductVariant().getProduct();
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    var firstImage = product.getImages().iterator().next();
                    response.setImageUrl(firstImage.getImageUrl());
                }
            }
        }
    }

    @AfterMapping
    default void calculateTotals(Cart cart, @MappingTarget CartResponse response) {
        BigDecimal cartTotal = BigDecimal.ZERO;

        if (response.getItems() != null) {
            for (CartItemResponse itemResponse : response.getItems()) {
                if (itemResponse.getPrice() != null && itemResponse.getQuantity() != null) {
                    BigDecimal subTotal = itemResponse.getPrice().multiply(new BigDecimal(itemResponse.getQuantity()));
                    itemResponse.setSubTotal(subTotal);
                    cartTotal = cartTotal.add(subTotal);
                }
            }
        }
        response.setCartTotal(cartTotal);
    }
}