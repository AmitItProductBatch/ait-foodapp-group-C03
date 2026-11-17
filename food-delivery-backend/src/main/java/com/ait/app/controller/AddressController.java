package com.ait.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ait.app.dto.AddressRequestDTO;
import com.ait.app.entity.Address;
import com.ait.app.service.AddressService;

@RestController
public class AddressController {

	@Autowired
	private AddressService addressService;

	@PreAuthorize("#userId == principal.id")
	@PostMapping("/api/users/{userId}/addresses")
	public Address createAddress(@PathVariable int userId, @RequestBody AddressRequestDTO dto) {

		return addressService.createAddress(userId, dto);
	}

	@PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
	@GetMapping("/api/users/{userId}/addresses")
	public java.util.List<com.ait.app.dto.AdressUpdateDto> getAddresses(@PathVariable int userId) {

		return addressService.getAddresses(userId);
	}

}