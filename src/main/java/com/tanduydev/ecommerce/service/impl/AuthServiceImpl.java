package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.auth.AuthLogin;
import com.tanduydev.ecommerce.dto.request.auth.AuthRegister;
import com.tanduydev.ecommerce.dto.request.auth.RefreshTokenRequest;
import com.tanduydev.ecommerce.dto.response.AuthResponse;
import com.tanduydev.ecommerce.dto.response.RefreshTokenResponse;
import com.tanduydev.ecommerce.enums.UserStatus;
import com.tanduydev.ecommerce.model.Customer;
import com.tanduydev.ecommerce.model.User;
import com.tanduydev.ecommerce.repository.RoleRepository;
import com.tanduydev.ecommerce.repository.UserRepository;
import com.tanduydev.ecommerce.service.AuthService;
import com.tanduydev.ecommerce.service.RefreshTokenService;
import com.tanduydev.ecommerce.util.AppConstants;
import com.tanduydev.ecommerce.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    @Transactional // QUAN TRỌNG: Đảm bảo nếu lưu User lỗi, thì không lưu Role/Customer
    public AuthResponse register(AuthRegister request) {
        log.info("[AUTH] Attempting to register user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("[AUTH] Registration failed: Email {} already exists", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setStatus(UserStatus.ACTIVE);
        customer.setRole(roleRepository.findByName(AppConstants.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Default role not found")));

        userRepository.save(customer);
        log.info("[AUTH] Successfully registered: {}", customer.getEmail());

        return AuthResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(customer))
                .refreshToken(jwtUtil.generateRefreshToken(customer))
                .email(customer.getEmail())
                .fullName(customer.getFullName())
                .role(customer.getRole().getName())
                .build();
    }

    @Override
    public AuthResponse login(AuthLogin request) {
        log.info("[AUTH] Attempting login for: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        String newRefreshToken = jwtUtil.generateRefreshToken(user);
        refreshTokenService.createRefreshToken(user, newRefreshToken);
        return AuthResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user))
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        refreshTokenService.validateToken(request.getRefreshToken());

        // Lấy thông tin user từ token cũ
        String email = jwtUtil.extractUsername(request.getRefreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Thu hồi token cũ và sinh cặp token mới
        refreshTokenService.revokeToken(request.getRefreshToken());

        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenService.createRefreshToken(user, newRefreshToken);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        log.info("[AUTH] User logged out successfully");
    }
}