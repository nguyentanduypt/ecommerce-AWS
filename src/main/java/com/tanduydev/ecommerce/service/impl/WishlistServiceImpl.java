package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.WishlistRequest;
import com.tanduydev.ecommerce.dto.response.WishlistResponse;
import com.tanduydev.ecommerce.mapper.WishlistMapper;
import com.tanduydev.ecommerce.model.Customer;
import com.tanduydev.ecommerce.model.Product;
import com.tanduydev.ecommerce.model.Wishlist;
import com.tanduydev.ecommerce.repository.ProductRepository;
import com.tanduydev.ecommerce.repository.UserRepository;
import com.tanduydev.ecommerce.repository.WishlistRepository;
import com.tanduydev.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    @Transactional
    public void addProductToWishlist(String email, WishlistRequest request) {
        log.info("[WISHLIST] User {} adding product {} to wishlist", email, request.getProductId());

        if (wishlistRepository.existsByCustomer_EmailAndProduct_Id(email, request.getProductId())) {
            throw new IllegalArgumentException("Product is already in your wishlist");
        }

        Customer customer = (Customer) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Wishlist wishlist = new Wishlist();
        wishlist.setCustomer(customer);
        wishlist.setProduct(product);

        wishlistRepository.save(wishlist);
    }

    @Override
    public List<WishlistResponse> getMyWishlist(String email) {
        List<Wishlist> wishlists = wishlistRepository.findAllByCustomer_Email(email);
        return wishlistMapper.toResponseList(wishlists);
    }

    @Override
    @Transactional
    public void removeProductFromWishlist(String email, UUID productId) {
        log.info("[WISHLIST] User {} removing product {} from wishlist", email, productId);

        if (!wishlistRepository.existsByCustomer_EmailAndProduct_Id(email, productId)) {
            throw new RuntimeException("Product not found in your wishlist");
        }

        wishlistRepository.deleteByCustomer_EmailAndProduct_Id(email, productId);
    }
}