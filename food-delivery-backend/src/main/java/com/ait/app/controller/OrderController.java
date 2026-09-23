package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ait.app.dto.OrderRequestDTO;
import com.ait.app.dto.OrderResponseDTO;
import com.ait.app.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping
	public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {

		OrderResponseDTO response = orderService.createOrder(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}