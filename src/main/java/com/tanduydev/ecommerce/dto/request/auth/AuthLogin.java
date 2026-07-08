package com.tanduydev.ecommerce.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLogin {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
