package com.ait.app.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.OrderItemRequestDTO;
import com.ait.app.dto.OrderRequestDTO;
import com.ait.app.dto.OrderResponseDTO;
import com.ait.app.entity.Order;
import com.ait.app.entity.OrderItem;
import com.ait.app.repository.OrderRepository;
import com.ait.app.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Override
	@Transactional
	public OrderResponseDTO createOrder(OrderRequestDTO request) {

		Order order = new Order();

		order.setUserId(request.getUserId());
		order.setRestaurantId(request.getRestaurantId());
		order.setDeliveryAddressSnapshot(request.getDeliveryAddressSnapshot());
		order.setTotalAmount(request.getTotalAmount());

		/*
		 * status automatically remains PLACED paymentStatus automatically remains
		 * PENDING
		 */

		for (OrderItemRequestDTO itemRequest : request.getOrderItems()) {

			OrderItem item = new OrderItem(
				itemRequest.getMenuItemId(),
				itemRequest.getItemNameSnapshot(),
				itemRequest.getUnitPrice(),
				itemRequest.getQuantity()
			);

			order.addOrderItem(item);
		}

		Order savedOrder = orderRepository.save(order);

		return convertToResponse(savedOrder);
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