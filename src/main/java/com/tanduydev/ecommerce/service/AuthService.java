package com.tanduydev.ecommerce.service;


import com.tanduydev.ecommerce.dto.request.auth.AuthLogin;
import com.tanduydev.ecommerce.dto.request.auth.AuthRegister;
import com.tanduydev.ecommerce.dto.request.auth.RefreshTokenRequest;
import com.tanduydev.ecommerce.dto.response.AuthResponse;
import com.tanduydev.ecommerce.dto.response.RefreshTokenResponse;

public interface AuthService {
    AuthResponse register(AuthRegister request);
    AuthResponse login(AuthLogin request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);
}
