package com.tanduydev.ecommerce.dto.request;

import com.tanduydev.ecommerce.enums.UserStatus;
import lombok.Data;

@Data
public class CustomerSearchRequest {
    private String keyword;
    private UserStatus status;
}