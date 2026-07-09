package com.tanduydev.ecommerce.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLogin {
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotBlank(message = "password không được để trống")
    private String password;
}
