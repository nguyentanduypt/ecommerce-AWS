package com.tanduydev.ecommerce.dto.request;

import com.tanduydev.ecommerce.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerStatusUpdateRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private UserStatus status;
}