package com.ait.app.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.AddressRequestDTO;
import com.ait.app.entity.Address;
import com.ait.app.entity.User;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.repository.AddressRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.AddressService;

@Service
public class AddressServiceimpl implements AddressService {

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Address createAddress(int userId, AddressRequestDTO dto) {

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Address address = new Address();
		address.setAddressLabel(dto.getAddressLabel());
		address.setStreetAddress(dto.getStreetAddress());
		address.setApartment(dto.getApartment());
		address.setLandmark(dto.getLandmark());
		address.setCity(dto.getCity());
		address.setPostalCode(dto.getPostalCode());
		address.setDeliveryInstructions(dto.getDeliveryInstructions());

		address.setUser(user);
		return addressRepository.save(address);
	}

	@Override
	public java.util.List<com.ait.app.dto.AdressUpdateDto> getAddresses(int userId) {

		java.util.List<com.ait.app.dto.AdressUpdateDto> result = new java.util.ArrayList<>();

		for (Address address : addressRepository.findByUserId(userId)) {

			com.ait.app.dto.AdressUpdateDto dto = new com.ait.app.dto.AdressUpdateDto();

			dto.setId(address.getId());
			dto.setAddressLabel(address.getAddressLabel());
			dto.setStreetAddress(address.getStreetAddress());
			dto.setApartment(address.getApartment());
			dto.setLandmark(address.getLandmark());
			dto.setCity(address.getCity());
			dto.setPostalCode(address.getPostalCode());
			dto.setDeliveryInstructions(address.getDeliveryInstructions());

			result.add(dto);
		}

		return result;
	}
}