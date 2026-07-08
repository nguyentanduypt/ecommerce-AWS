package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.WishlistRequest;
import com.tanduydev.ecommerce.dto.response.WishlistResponse;

import java.util.List;
import java.util.UUID;

public interface WishlistService {
    void addProductToWishlist(String email, WishlistRequest request);
    List<WishlistResponse> getMyWishlist(String email);
    void removeProductFromWishlist(String email, UUID productId);
}
