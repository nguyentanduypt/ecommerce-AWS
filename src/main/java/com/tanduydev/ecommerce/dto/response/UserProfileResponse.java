package com.tanduydev.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String email;
    private String fullName;
    private String avatarUrl;
    private String role;
    private String phone;
}