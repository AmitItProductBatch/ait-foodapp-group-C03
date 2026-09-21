package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.PriceCalculationRequestDTO;
import com.ait.app.dto.PriceCalculationResponseDTO;
import com.ait.app.dto.PriceResponseDTO;
import com.ait.app.service.MenuItemService;
import com.ait.app.service.PriceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

	@Autowired
	private MenuItemService menuItemService;

	@Autowired
	private PriceService priceService;

	@GetMapping("/{itemId}")
	public ResponseEntity<PriceResponseDTO> getItemPrice(@PathVariable int itemId) {
		PriceResponseDTO response = menuItemService.getItemPrice(itemId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/calculate")
	public ResponseEntity<PriceCalculationResponseDTO> calculatePrice(
			@Valid @RequestBody PriceCalculationRequestDTO request) {

		PriceCalculationResponseDTO response = priceService.calculatePrice(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
