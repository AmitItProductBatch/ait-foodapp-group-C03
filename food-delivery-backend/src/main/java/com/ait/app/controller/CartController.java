package com.ait.app.controller;

import com.ait.app.security.AppUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ait.app.dto.CartRequestDTO;
import com.ait.app.dto.CartResponseDTO;
import com.ait.app.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@PreAuthorize("#request.userId == principal.id")
	@PostMapping
	public ResponseEntity<CartResponseDTO> createCart(@Valid @RequestBody CartRequestDTO request) {

		CartResponseDTO response = cartService.createCart(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<CartResponseDTO> getMyCart(@AuthenticationPrincipal AppUserDetails currentUser) {

		Integer userId = currentUser.getId();

		CartResponseDTO response = cartService.getMyCart(userId);

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("#userId == principal.id")
	@DeleteMapping
	public ResponseEntity<Void> clearCart(@RequestParam Integer userId) {

		cartService.clearCart(userId);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("#userId == principal.id")
	@DeleteMapping("/items/{itemId}")
	public ResponseEntity<Void> deleteCartItem(@PathVariable Integer itemId, @RequestParam Integer userId) {

		cartService.deleteCartItem(itemId, userId);

		return ResponseEntity.noContent().build();
	}
}
