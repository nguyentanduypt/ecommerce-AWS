package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.model.RefreshToken;
import com.tanduydev.ecommerce.model.User;
import com.tanduydev.ecommerce.repository.RefreshTokenRepository;
import com.tanduydev.ecommerce.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // IMPORT CHUẨN CỦA SPRING
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Lấy giá trị thời gian sống của Refresh Token từ file yml
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenDurationMs;

    @Override
    public void createRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token); // Dùng đúng tên hàm setter của model
        refreshToken.setRevoked(false);

        // Model dùng LocalDateTime nên ở đây cũng dùng LocalDateTime
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plus(refreshTokenDurationMs, ChronoUnit.MILLIS);
        refreshToken.setExpiryDate(expiry);

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void revokeToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại trong hệ thống!"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void validateToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ!"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token này đã bị thu hồi (revoked)!");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token đã hết hạn, vui lòng đăng nhập lại!");
        }
    }
}