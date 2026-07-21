package com.tanduydev.ecommerce.controller;

import com.tanduydev.ecommerce.dto.ApiResponse;
import com.tanduydev.ecommerce.dto.request.WishlistRequest;
import com.tanduydev.ecommerce.dto.response.WishlistResponse;
import com.tanduydev.ecommerce.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class WishlistController {

    private final WishlistService wishlistService;

    // Xem danh sách yêu thích của bản thân
    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getMyWishlist(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Fetched wishlist successfully",
                wishlistService.getMyWishlist(principal.getName())));
    }

    // Thêm vào danh sách yêu thích
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addToWishlist(Principal principal, @Valid @RequestBody WishlistRequest request) {
        wishlistService.addProductToWishlist(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Added to wishlist successfully", null));
    }

    // Xóa khỏi danh sách yêu thích
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(Principal principal, @PathVariable UUID productId) {
        wishlistService.removeProductFromWishlist(principal.getName(), productId);
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist successfully", null));
    }
}