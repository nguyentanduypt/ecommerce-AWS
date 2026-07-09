package com.tanduydev.ecommerce.dto.response;

import com.tanduydev.ecommerce.enums.Gender;
import com.tanduydev.ecommerce.enums.UserStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class CustomerResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private UserStatus status;
    private Gender genderEnum;
    private String roleName;
    // Nếu bạn cần trả về địa chỉ mặc định, có thể thêm 1 field:
    // private List<AddressResponse> addresses;
}