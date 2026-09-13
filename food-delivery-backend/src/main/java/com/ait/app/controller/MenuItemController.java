package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.MenuItemRequestDTO;
import com.ait.app.dto.MenuItemResponseDTO;
import com.ait.app.dto.MenuItemUpdateDTO;
import com.ait.app.service.MenuItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class MenuItemController {

	@Autowired
	private MenuItemService menuItemService;

	@PostMapping("/{restaurantId}/menu")
	public ResponseEntity<MenuItemResponseDTO> createMenuItem(
			@PathVariable int restaurantId,
			@Valid @RequestBody MenuItemRequestDTO requestDTO) {

		MenuItemResponseDTO response = menuItemService.createMenuItem(restaurantId, requestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}	
		 @PutMapping("/menu/{itemId}")
		    public ResponseEntity<MenuItemResponseDTO> updateMenuItem( @PathVariable Integer itemId, @Valid @RequestBody MenuItemUpdateDTO updateDTO,
		            @RequestParam Integer adminId) {

		        MenuItemResponseDTO response = menuItemService.updateMenuItem(itemId,updateDTO,adminId);

		        return ResponseEntity.status(HttpStatus.OK).body(response);
		
	
		
	}
}
