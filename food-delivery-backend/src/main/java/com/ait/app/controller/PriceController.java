package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.PriceResponseDTO;
import com.ait.app.service.MenuItemService;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

	@Autowired
	private MenuItemService menuItemService;

	@GetMapping("/{itemId}")
	public ResponseEntity<PriceResponseDTO> getItemPrice(@PathVariable int itemId) {
		PriceResponseDTO response = menuItemService.getItemPrice(itemId);
		if (response == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(response);
	}
}
