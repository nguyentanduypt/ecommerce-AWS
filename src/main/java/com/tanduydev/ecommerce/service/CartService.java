package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.cart.CartItemRequest;
import com.tanduydev.ecommerce.dto.response.cart.CartResponse;

import java.util.UUID;

public interface CartService {
    CartResponse getMyCart(String email);
    CartResponse addToCart(String email, CartItemRequest request);
    CartResponse updateQuantity(String email, UUID cartItemId, Integer quantity);
    CartResponse removeFromCart(String email, UUID cartItemId);
    CartResponse clearCart(String email);
}
