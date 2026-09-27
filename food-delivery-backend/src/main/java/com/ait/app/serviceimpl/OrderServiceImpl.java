package com.ait.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.OrderHistoryDTO;
import com.ait.app.dto.OrderHistoryResponseDTO;
import com.ait.app.dto.OrderStatusUpdateDTO;
import com.ait.app.dto.OrderStatusUpdateResponseDTO;
import com.ait.app.entity.Order;
import com.ait.app.entity.OrderStatusHistory;
import com.ait.app.enums.OrderStatus;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.exception.UnauthorizedActionException;
import com.ait.app.repository.OrderRepository;
import com.ait.app.repository.OrderStatusHistoryRepository;
import com.ait.app.repository.RestaurantRepository;
import com.ait.app.service.NotificationService;
import com.ait.app.service.OrderService;
import com.ait.app.util.OrderAuthorizationHelper;
import com.ait.app.util.OrderStatusTransitionValidator;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderStatusHistoryRepository orderStatusHistoryRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private NotificationService notificationService;

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

	@Override
	@Transactional
	public OrderStatusUpdateResponseDTO updateOrderStatus(Integer orderId, OrderStatusUpdateDTO statusUpdateDTO, Integer userId,
			String userRole) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		OrderStatus currentStatus = order.getStatus();
		OrderStatus newStatus = statusUpdateDTO.getStatus();

		if (currentStatus == newStatus) {
			throw new InvalidRequestException("Order is already in status: " + newStatus);
		}

		if (!OrderStatusTransitionValidator.isValidTransition(currentStatus, newStatus)) {
			throw new InvalidRequestException(OrderStatusTransitionValidator.getErrorMessage(currentStatus, newStatus));
		}

		try {
			OrderAuthorizationHelper.verifyRestaurantOrAdminAuthorization(order, userId, userRole,
					restaurantRepository);
		} catch (UnauthorizedActionException e) {
			throw e;
		}

		OrderStatus fromStatus = currentStatus;
		order.setStatus(newStatus);

		OrderStatusHistory statusHistory = new OrderStatusHistory(order, fromStatus, newStatus, userId);
		order.addStatusHistory(statusHistory);

		orderRepository.save(order);
		orderStatusHistoryRepository.save(statusHistory);

		notificationService.sendOrderStatusUpdateNotification(order, fromStatus, newStatus);

		return convertToStatusUpdateResponseDTO(order);
	}

	private OrderStatusUpdateResponseDTO convertToStatusUpdateResponseDTO(Order order) {
		OrderStatusUpdateResponseDTO dto = new OrderStatusUpdateResponseDTO();
		dto.setId(order.getId());
		dto.setUserId(order.getUserId());
		dto.setRestaurantId(order.getRestaurantId());
		dto.setDeliveryAddressSnapshot(order.getDeliveryAddressSnapshot());
		dto.setTotalAmount(order.getTotalAmount());
		dto.setStatus(order.getStatus());
		dto.setPaymentStatus(order.getPaymentStatus());
		dto.setPaymentMethod(order.getPaymentMethod());
		dto.setCreatedAt(order.getCreatedAt());
		dto.setUpdatedAt(order.getUpdatedAt());
		return dto;
	}
}
