package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.RestaurantRequestDTO;
import com.ait.app.dto.RestaurantResponseDTO;
import com.ait.app.service.RestaurantService;

//import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
@Validated
public class RestaurantController {

	@Autowired
	RestaurantService restaurantService;

	@PostMapping
	public ResponseEntity<RestaurantResponseDTO> createRestaurant(

			// @Valid
			@RequestBody 
			RestaurantRequestDTO requestDTO) {

		RestaurantResponseDTO response = restaurantService.createRestaurant(requestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}