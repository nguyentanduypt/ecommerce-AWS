package com.tanduydev.ecommerce.dto.request;

import com.tanduydev.ecommerce.enums.Gender;
import com.tanduydev.ecommerce.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Email is required")
    private String phone;
    private Gender genderEnum;
    @NotBlank(message = "Email is required")
    private String password;
    private UserStatus status;
}