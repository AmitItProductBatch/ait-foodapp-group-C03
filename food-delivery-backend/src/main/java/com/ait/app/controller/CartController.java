package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ait.app.dto.CartRequestDTO;
import com.ait.app.dto.CartResponseDTO;
import com.ait.app.service.CartService;

import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponseDTO> createCart(
            @Valid @RequestBody CartRequestDTO request) {

        CartResponseDTO response = cartService.createCart(request);

        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> deleteCartItem(
            @PathVariable Integer itemId,
            @RequestParam Integer userId) {

        try {
            cartService.deleteCartItem(itemId, userId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}