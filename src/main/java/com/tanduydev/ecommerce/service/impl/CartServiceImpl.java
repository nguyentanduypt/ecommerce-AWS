package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.cart.CartItemRequest;
import com.tanduydev.ecommerce.dto.response.cart.CartResponse;
import com.tanduydev.ecommerce.mapper.CartMapper;
import com.tanduydev.ecommerce.model.Cart;
import com.tanduydev.ecommerce.model.CartItem;
import com.tanduydev.ecommerce.model.Customer;
import com.tanduydev.ecommerce.model.ProductVariant;
import com.tanduydev.ecommerce.repository.CartRepository;
import com.tanduydev.ecommerce.repository.ProductVariantRepository;
import com.tanduydev.ecommerce.repository.UserRepository;
import com.tanduydev.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    // Hàm tiện ích: Lấy giỏ hàng, nếu chưa có thì tạo mới
    private Cart getOrCreateCart(String email) {
        return cartRepository.findByCustomer_Email(email).orElseGet(() -> {
            Customer customer = (Customer) userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            Cart newCart = new Cart();
            newCart.setCustomer(customer);
            newCart.setCartItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });
    }

    @Override
    @Transactional
    public CartResponse getMyCart(String email) {
        Cart cart = getOrCreateCart(email);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(String email, CartItemRequest request) {
        Cart cart = getOrCreateCart(email);
        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        // Tìm xem sản phẩm đã có trong giỏ chưa
        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(request.getVariantId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Nếu có -> Cộng số lượng
            CartItem item = existingItemOpt.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (newQuantity > variant.getStock()) {
                throw new IllegalArgumentException("Not enough stock. Remaining: " + variant.getStock());
            }
            item.setQuantity(newQuantity);
        } else {
            // Nếu chưa -> Thêm mới
            if (request.getQuantity() > variant.getStock()) {
                throw new IllegalArgumentException("Not enough stock. Remaining: " + variant.getStock());
            }
            CartItem newItem = new CartItem();
            newItem.setCart(cart); // Bắt buộc set để duy trì quan hệ 2 chiều
            newItem.setProductVariant(variant);
            newItem.setQuantity(request.getQuantity());

            cart.getCartItems().add(newItem);
        }

        // Nhờ CascadeType.ALL, chỉ cần save Cart là lưu luôn CartItem
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(String email, UUID cartItemId, Integer quantity) {
        Cart cart = getOrCreateCart(email);

        CartItem itemToUpdate = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (quantity > itemToUpdate.getProductVariant().getStock()) {
            throw new IllegalArgumentException("Not enough stock. Remaining: " + itemToUpdate.getProductVariant().getStock());
        }

        itemToUpdate.setQuantity(quantity);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(String email, UUID cartItemId) {
        Cart cart = getOrCreateCart(email);

        // orphanRemoval = true sẽ tự động xóa CartItem dưới Database khi ta remove nó khỏi List
        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getCartItems().clear(); // Xóa sạch list -> orphanRemoval xóa sạch DB
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }
}