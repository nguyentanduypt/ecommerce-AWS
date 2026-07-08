package com.tanduydev.ecommerce.controller;

import com.tanduydev.ecommerce.dto.ApiResponse;
import com.tanduydev.ecommerce.dto.request.AddressRequest;
import com.tanduydev.ecommerce.dto.response.AddressResponse;
import com.tanduydev.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(Principal principal, @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Address created successfully",
                addressService.createAddress(principal.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Fetched addresses successfully",
                addressService.getMyAddresses(principal.getName())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(Principal principal, @PathVariable UUID id, @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully",
                addressService.updateAddress(principal.getName(), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(Principal principal, @PathVariable UUID id) {
        addressService.deleteAddress(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }
}