package com.ait.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.RestaurantDetailsDTO;
import com.ait.app.dto.RestaurantListResponseDTO;
import com.ait.app.dto.RestaurantRequestDTO;
import com.ait.app.dto.RestaurantResponseDTO;
import com.ait.app.service.RestaurantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

	@Autowired
	RestaurantService restaurantService;

	@PreAuthorize("#requestDTO.ownerId == principal.id")
	@PostMapping
	public ResponseEntity<RestaurantResponseDTO> createRestaurant(@Valid @RequestBody RestaurantRequestDTO requestDTO) {
		RestaurantResponseDTO response = restaurantService.createRestaurant(requestDTO);

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

	/**
	 * Restaurants of the logged-in PARTNER, or all restaurants for an ADMIN
	 * (includes approval state).
	 */
	@GetMapping("/manage")
	public ResponseEntity<List<com.ait.app.dto.RestaurantManageDTO>> getManagedRestaurants(
			@org.springframework.security.core.annotation.AuthenticationPrincipal com.ait.app.security.AppUserDetails currentUser) {

		return ResponseEntity.ok(restaurantService.getManagedRestaurants(currentUser.getId(), currentUser.getRole()));
	}

	@GetMapping("/{id}/menu")
	public ResponseEntity<com.ait.app.dto.RestaurantMenuResponseDTO> getRestaurantMenu(@PathVariable Integer id) {

		return ResponseEntity.ok(restaurantService.getRestaurantMenu(id));
	}

	@GetMapping("/{id}/menu/manage")
	public ResponseEntity<List<com.ait.app.dto.MenuItemResponseDTO>> getManagedMenu(@PathVariable Integer id,
			@org.springframework.security.core.annotation.AuthenticationPrincipal com.ait.app.security.AppUserDetails currentUser) {

		return ResponseEntity.ok(restaurantService.getManagedMenu(id, currentUser.getId(), currentUser.getRole()));
	}

	@org.springframework.web.bind.annotation.PutMapping("/{id}/approve")
	public ResponseEntity<com.ait.app.dto.RestaurantManageDTO> approveRestaurant(@PathVariable Integer id) {

		return ResponseEntity.ok(restaurantService.updateApproval(id, true));
	}

	@org.springframework.web.bind.annotation.PutMapping("/{id}/reject")
	public ResponseEntity<com.ait.app.dto.RestaurantManageDTO> rejectRestaurant(@PathVariable Integer id) {

		return ResponseEntity.ok(restaurantService.updateApproval(id, false));
	}

	@GetMapping("/{id}")
	public ResponseEntity<RestaurantDetailsDTO> getRestaurantDetails(@PathVariable Integer id) {

		RestaurantDetailsDTO response = restaurantService.getRestaurantDetails(id);

		return ResponseEntity.ok(response);
	}
}
