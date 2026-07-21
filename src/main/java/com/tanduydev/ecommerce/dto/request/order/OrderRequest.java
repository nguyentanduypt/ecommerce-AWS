package com.tanduydev.ecommerce.dto.request.order;

import com.tanduydev.ecommerce.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderRequest {

    @NotNull(message = "Address ID is required")
    private UUID addressId;
    private String note;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String couponCode;
}