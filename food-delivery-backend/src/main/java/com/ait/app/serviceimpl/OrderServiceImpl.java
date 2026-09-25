package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

import com.ait.app.dto.OrderValidationItemDTO;
import com.ait.app.dto.OrderValidationRequestDTO;
import com.ait.app.dto.OrderValidationResponseDTO;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.dto.OrderItemRequestDTO;
import com.ait.app.dto.OrderRequestDTO;
import com.ait.app.dto.OrderResponseDTO;

import com.ait.app.entity.Order;
import com.ait.app.entity.OrderItem;
import com.ait.app.repository.OrderRepository;
import com.ait.app.service.OrderService;
import com.ait.app.service.OrderValidationService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderValidationService orderValidationService;

	@Override
	@Transactional
	public OrderResponseDTO createOrder(OrderRequestDTO request) {

		OrderValidationRequestDTO validationRequest = createValidationRequest(request);
		OrderValidationResponseDTO validateResponse = orderValidationService.validateOrder(validationRequest);
		if (!validateResponse.isValid()) {
			String errorMessage = String.join(", ", validateResponse.getErrors());
			throw new InvalidRequestException(errorMessage);
		}

		Order order = new Order();

		order.setUserId(request.getUserId());
		order.setRestaurantId(request.getRestaurantId());
		order.setDeliveryAddressSnapshot(request.getDeliveryAddressSnapshot());
		order.setTotalAmount(request.getTotalAmount());

		for (OrderItemRequestDTO itemRequest : request.getOrderItems()) {

			OrderItem item = new OrderItem();

			item.setMenuItemId(itemRequest.getMenuItemId());
			item.setItemName(itemRequest.getItemName());
			item.setUnitPrice(itemRequest.getUnitPrice());
			item.setQuantity(itemRequest.getQuantity());
			item.setSubtotal(itemRequest.getSubtotal());

			order.addOrderItem(item);
		}

		Order savedOrder = orderRepository.save(order);

		return convertToResponse(savedOrder);
	}

	private OrderValidationRequestDTO createValidationRequest(OrderRequestDTO request) {

		OrderValidationRequestDTO validationRequest = new OrderValidationRequestDTO();

		validationRequest.setUserId(request.getUserId());

		validationRequest.setRestaurantId(request.getRestaurantId());

		List<OrderValidationItemDTO> validationItems = new ArrayList<>();

		for (OrderItemRequestDTO itemRequest : request.getOrderItems()) {

			OrderValidationItemDTO item = new OrderValidationItemDTO();

			item.setMenuItemId(itemRequest.getMenuItemId());

			item.setQuantity(itemRequest.getQuantity());

			item.setUnitPrice(itemRequest.getUnitPrice());

			validationItems.add(item);
		}

		validationRequest.setItems(validationItems);

		return validationRequest;

	}

	private OrderResponseDTO convertToResponse(Order order) {

		OrderResponseDTO response = new OrderResponseDTO();

		response.setId(order.getId());
		response.setUserId(order.getUserId());
		response.setRestaurantId(order.getRestaurantId());
		response.setDeliveryAddressSnapshot(order.getDeliveryAddressSnapshot());
		response.setTotalAmount(order.getTotalAmount());
		response.setStatus(order.getStatus());
		response.setPaymentStatus(order.getPaymentStatus());
		response.setCreatedAt(order.getCreatedAt());
		response.setUpdatedAt(order.getUpdatedAt());

		return response;
	}
}