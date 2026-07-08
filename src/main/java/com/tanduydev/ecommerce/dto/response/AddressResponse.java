package com.tanduydev.ecommerce.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class AddressResponse {
    private UUID id;
    private String receiverName;
    private String receiverPhone;
    private String detailAddress;
    private Boolean isDefault;
}