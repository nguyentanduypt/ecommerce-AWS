package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.request.CustomerRequest;
import com.tanduydev.ecommerce.dto.response.CustomerResponse;
import com.tanduydev.ecommerce.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // Ánh xạ tên Role từ entity Role bên trong User
    @Mapping(target = "roleName", source = "role.name")
    CustomerResponse toResponse(Customer customer);

    // Bỏ qua các trường không được phép update trực tiếp qua API này
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(@MappingTarget Customer customer, CustomerRequest request);
}