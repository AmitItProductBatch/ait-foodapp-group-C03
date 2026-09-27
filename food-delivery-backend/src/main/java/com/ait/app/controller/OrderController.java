package com.ait.app.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.OrderHistoryResponseDTO;
import com.ait.app.dto.OrderStatusUpdateDTO;
import com.ait.app.dto.OrderStatusUpdateResponseDTO;
import com.ait.app.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/user/{userId}")
	public ResponseEntity<OrderHistoryResponseDTO> getUserOrderHistory(@PathVariable Integer userId,

			@RequestParam(defaultValue = "0") int page,

			@RequestParam(defaultValue = "10") int size,

			@RequestParam(required = false) String status,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {

		OrderHistoryResponseDTO response = orderService.getUserOrderHistory(userId, status, fromDate, toDate, page,
				size);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/{orderId}/status")
	public ResponseEntity<OrderStatusUpdateResponseDTO> updateOrderStatus(@PathVariable Integer orderId,
			@Valid @RequestBody OrderStatusUpdateDTO statusUpdateDTO,
			@RequestHeader(value = "X-User-Id", required = false) Integer userId,
			@RequestHeader(value = "X-User-Role", required = false, defaultValue = "USER") String userRole) {

		OrderStatusUpdateResponseDTO response = orderService.updateOrderStatus(orderId, statusUpdateDTO, userId,
				userRole);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}