package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.ait.app.dto.CartItemRequestDTO;
import com.ait.app.dto.CartItemResponseDTO;
import com.ait.app.service.CartItemService;

@RestController
@RequestMapping("/api/cart/items")
@Validated
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<CartItemResponseDTO> createCartItem(
            @Valid @RequestBody CartItemRequestDTO dto) {

        CartItemResponseDTO response =cartItemService.createCartItem(dto);

        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
}