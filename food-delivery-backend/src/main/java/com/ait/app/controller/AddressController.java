package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.AddressRequestDTO;
import com.ait.app.entity.Address;
import com.ait.app.service.AddressService;

@RestController
public class AddressController {

	@Autowired
	AddressService addressService;


	@PostMapping("/api/users/{userId}/addresses")
	public Address createAddress(
			@PathVariable int userId,
			@RequestBody AddressRequestDTO dto) {

		return addressService.createAddress(userId, dto);
	}

}