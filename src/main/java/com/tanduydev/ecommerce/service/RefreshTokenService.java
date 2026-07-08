package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.model.User;

public interface RefreshTokenService {
    void createRefreshToken(User user, String refreshTokenRequest);
    void revokeToken (String refreshToken);
    void validateToken(String refreshToken);
}
