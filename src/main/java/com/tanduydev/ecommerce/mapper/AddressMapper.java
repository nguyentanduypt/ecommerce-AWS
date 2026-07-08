package com.tanduydev.ecommerce.mapper;

import com.tanduydev.ecommerce.dto.request.AddressRequest;
import com.tanduydev.ecommerce.dto.response.AddressResponse;
import com.tanduydev.ecommerce.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressRequest request);
    AddressResponse toResponse(Address address);
    List<AddressResponse> toResponseList(List<Address> addresses);
    void updateEntity(@MappingTarget Address address, AddressRequest request);
}