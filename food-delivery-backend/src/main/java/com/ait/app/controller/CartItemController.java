package com.ait.app.controller;

import com.ait.app.security.AppUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.ait.app.dto.CartItemRequestDTO;
import com.ait.app.dto.CartItemResponseDTO;
import com.ait.app.dto.CartResponseDTO;
import com.ait.app.dto.UpdateQuantityDTO;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.service.CartItemService;

@RestController
@RequestMapping("/api/cart/items")
@Validated
public class CartItemController {

	@Autowired
	private CartItemService cartItemService;

	@PostMapping
	public ResponseEntity<CartItemResponseDTO> createCartItem(@Valid @RequestBody CartItemRequestDTO dto,
			@AuthenticationPrincipal AppUserDetails currentUser) {

		CartItemResponseDTO response = cartItemService.createCartItem(dto, currentUser.getId());

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PreAuthorize("#userId == principal.id")
	@PutMapping("/{itemId}")
	public ResponseEntity<?> updateCartItemQuantity(@PathVariable Integer itemId, @RequestParam Integer userId,
			@Valid @RequestBody UpdateQuantityDTO dto) {

		try {
			CartResponseDTO response = cartItemService.updateCartItemQuantity(itemId, userId, dto);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (InvalidRequestException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (ResourceNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}