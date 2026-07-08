package com.tanduydev.ecommerce.controller;

import com.tanduydev.ecommerce.dto.ApiResponse;
import com.tanduydev.ecommerce.dto.request.cart.CartItemRequest;
import com.tanduydev.ecommerce.dto.response.cart.CartResponse;
import com.tanduydev.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Fetched cart successfully",
                cartService.getMyCart(principal.getName())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            Principal principal,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Added to cart",
                cartService.addToCart(principal.getName(), request)));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            Principal principal,
            @PathVariable UUID itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(ApiResponse.success("Cart updated",
                cartService.updateQuantity(principal.getName(), itemId, quantity)));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(Principal principal, @PathVariable UUID itemId) {
        return ResponseEntity.ok(ApiResponse.success("Removed from cart",
                cartService.removeFromCart(principal.getName(), itemId)));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Cart cleared",
                cartService.clearCart(principal.getName())));
    }
}