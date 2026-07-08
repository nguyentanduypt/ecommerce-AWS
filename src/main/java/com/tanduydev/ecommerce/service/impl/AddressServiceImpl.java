package com.tanduydev.ecommerce.service.impl;

import com.tanduydev.ecommerce.dto.request.AddressRequest;
import com.tanduydev.ecommerce.dto.response.AddressResponse;
import com.tanduydev.ecommerce.mapper.AddressMapper;
import com.tanduydev.ecommerce.model.Address;
import com.tanduydev.ecommerce.model.Customer;
import com.tanduydev.ecommerce.repository.AddressRepository;
import com.tanduydev.ecommerce.repository.UserRepository;
import com.tanduydev.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponse createAddress(String email, AddressRequest request) {
        log.info("[ADDRESS] Creating new address for user: {}", email);

        Customer customer = (Customer) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Address address = addressMapper.toEntity(request);
        address.setCustomer(customer);

        handleDefaultAddressLogic(email, address, request.getIsDefault());

        addressRepository.save(address);
        log.info("[ADDRESS] Created address successfully: {}", address.getId());
        return addressMapper.toResponse(address);
    }

    @Override
    public List<AddressResponse> getMyAddresses(String email) {
        List<Address> addresses = addressRepository.findAllByCustomer_Email(email);
        return addressMapper.toResponseList(addresses);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(String email, UUID addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndCustomer_Email(addressId, email)
                .orElseThrow(() -> new RuntimeException("Address not found or access denied"));

        addressMapper.updateEntity(address, request);
        handleDefaultAddressLogic(email, address, request.getIsDefault());

        addressRepository.save(address);
        log.info("[ADDRESS] Updated address successfully: {}", address.getId());
        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(String email, UUID addressId) {
        Address address = addressRepository.findByIdAndCustomer_Email(addressId, email)
                .orElseThrow(() -> new RuntimeException("Address not found or access denied"));

        addressRepository.delete(address);
        log.info("[ADDRESS] Deleted address successfully: {}", addressId);
    }

    private void handleDefaultAddressLogic(String email, Address currentAddress, Boolean isDefaultRequest) {
        if (Boolean.TRUE.equals(isDefaultRequest)) {
            List<Address> defaultAddresses = addressRepository.findAllByCustomer_EmailAndIsDefaultTrue(email);
            for (Address oldDefault : defaultAddresses) {
                if (!oldDefault.getId().equals(currentAddress.getId())) {
                    oldDefault.setIsDefault(false);
                    addressRepository.save(oldDefault);
                }
            }
            currentAddress.setIsDefault(true);
        } else if (currentAddress.getIsDefault() == null) {
            currentAddress.setIsDefault(false);
        }
    }
}