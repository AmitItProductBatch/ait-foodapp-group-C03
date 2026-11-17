package com.ait.app.service;

import com.ait.app.dto.AddressRequestDTO;
import com.ait.app.entity.Address;

public interface AddressService {

	Address createAddress(int userId, AddressRequestDTO dto);

	java.util.List<com.ait.app.dto.AdressUpdateDto> getAddresses(int userId);
}