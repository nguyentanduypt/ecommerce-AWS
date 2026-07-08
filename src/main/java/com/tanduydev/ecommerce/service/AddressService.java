package com.tanduydev.ecommerce.service;

import com.tanduydev.ecommerce.dto.request.AddressRequest;
import com.tanduydev.ecommerce.dto.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse createAddress(String email, AddressRequest request);
    List<AddressResponse> getMyAddresses(String email);
    AddressResponse updateAddress(String email, UUID addressId, AddressRequest request);
    void deleteAddress(String email, UUID addressId);
}