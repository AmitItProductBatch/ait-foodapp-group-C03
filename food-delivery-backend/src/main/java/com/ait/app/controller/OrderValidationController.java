package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.OrderValidationRequestDTO;
import com.ait.app.dto.OrderValidationResponseDTO;
import com.ait.app.service.OrderValidationService;

@RestController
@RequestMapping("/api/orders")
public class OrderValidationController {

	@Autowired
	private OrderValidationService orderValidationService;

	@PostMapping("/validate")
	public ResponseEntity<OrderValidationResponseDTO> validateOrder(@RequestBody OrderValidationRequestDTO request) {
		OrderValidationResponseDTO response = orderValidationService.validateOrder(request);
		return ResponseEntity.ok(response);
	}
}
