package com.ait.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ait.app.dto.OrderHistoryDTO;
import com.ait.app.dto.OrderHistoryResponseDTO;
import com.ait.app.entity.Order;
import com.ait.app.enums.OrderStatus;
import com.ait.app.repository.OrderRepository;
import com.ait.app.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public OrderHistoryResponseDTO getUserOrderHistory(Integer userId, String status, LocalDateTime fromDate,
			LocalDateTime toDate, int page, int size) {

		if (page < 0) {
			page = 0;
		}

		if (size <= 0) {
			size = 10;
		}

		if (size > 100) {
			size = 100;
		}

		Pageable pageable = PageRequest.of(page, size);

		Page<Order> orderPage;

		OrderStatus orderStatus = null;

		if (status != null && !status.isBlank()) {
			try {
				orderStatus = OrderStatus.valueOf(status.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid order status: " + status);
			}
		}

		if (orderStatus != null && fromDate != null && toDate != null) {

			orderPage = orderRepository.findByUserIdAndStatusAndCreatedAtBetweenOrderByCreatedAtDesc(userId,
					orderStatus, fromDate, toDate, pageable);

		}

		else if (orderStatus != null) {

			orderPage = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, orderStatus, pageable);

		}

		else if (fromDate != null && toDate != null) {

			orderPage = orderRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, fromDate, toDate,
					pageable);

		}

		else {

			orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
		}

		List<OrderHistoryDTO> orders = orderPage.getContent().stream().map(this::convertToHistoryDTO)
				.collect(Collectors.toList());

		return new OrderHistoryResponseDTO(orders, orderPage.getNumber(), orderPage.getSize(),
				orderPage.getTotalElements(), orderPage.getTotalPages());
	}

	private OrderHistoryDTO convertToHistoryDTO(Order order) {

		return new OrderHistoryDTO(order.getId(), order.getUserId(), order.getRestaurantId(), order.getTotalAmount(),
				order.getStatus().toString(), order.getPaymentStatus().toString(), order.getCreatedAt(),
				order.getUpdatedAt());
	}
}
