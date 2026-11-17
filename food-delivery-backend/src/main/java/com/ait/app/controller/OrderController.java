package com.ait.app.controller;

import com.ait.app.security.AppUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.dto.OrderDetailDTO;
import com.ait.app.dto.OrderHistoryResponseDTO;
import com.ait.app.dto.OrderStatusUpdateDTO;
import com.ait.app.dto.OrderStatusUpdateResponseDTO;
import com.ait.app.dto.OrderRequestDTO;
import com.ait.app.dto.OrderResponseDTO;
import com.ait.app.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PreAuthorize("#request.userId == principal.id")
	@PostMapping
	public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {

		OrderResponseDTO response = orderService.createOrder(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
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
			@AuthenticationPrincipal AppUserDetails currentUser) {

		OrderStatusUpdateResponseDTO response = orderService.updateOrderStatus(orderId, statusUpdateDTO,
				currentUser.getId(), currentUser.getRole());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<OrderHistoryResponseDTO> getRestaurantOrders(@PathVariable Integer restaurantId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String status, @AuthenticationPrincipal AppUserDetails currentUser) {

		return ResponseEntity.ok(orderService.getRestaurantOrders(restaurantId, currentUser.getId(),
				currentUser.getRole(), status, page, size));
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDetailDTO> getOrderById(@PathVariable Integer orderId,
			@AuthenticationPrincipal AppUserDetails currentUser) {

		OrderDetailDTO response = orderService.getOrderById(orderId, currentUser.getId(), currentUser.getRole());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}