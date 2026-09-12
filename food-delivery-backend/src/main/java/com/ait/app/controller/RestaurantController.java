package com.ait.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.RestaurantListResponseDTO;
import com.ait.app.dto.RestaurantRequestDTO;
import com.ait.app.dto.RestaurantResponseDTO;
import com.ait.app.service.RestaurantService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;


    @PostMapping
    public ResponseEntity<RestaurantResponseDTO> createRestaurant(
            
    @Valid@RequestBody RestaurantRequestDTO requestDTO) {

        RestaurantResponseDTO response =restaurantService.createRestaurant(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
	public ResponseEntity<List<RestaurantListResponseDTO>> getAllRestaurants() {
		List<RestaurantListResponseDTO> response = restaurantService.getAllRestaurants();

		return ResponseEntity.ok(response);

	}

	@GetMapping("/cuisine/{cuisine}")
	public ResponseEntity<List<RestaurantListResponseDTO>> getRestaurantsByCuisine(@PathVariable String cuisine) {
		List<RestaurantListResponseDTO> response = restaurantService.getRestaurantsByCuisine(cuisine);

		return ResponseEntity.ok(response);
	}
}