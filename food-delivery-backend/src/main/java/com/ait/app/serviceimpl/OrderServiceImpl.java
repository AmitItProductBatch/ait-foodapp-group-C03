package com.ait.app.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.OrderDetailDTO;
import com.ait.app.dto.OrderHistoryDTO;
import com.ait.app.dto.OrderHistoryResponseDTO;
import com.ait.app.dto.OrderItemDetailDTO;
import com.ait.app.dto.OrderStatusHistoryDTO;
import com.ait.app.dto.OrderStatusUpdateDTO;
import com.ait.app.dto.OrderStatusUpdateResponseDTO;
import com.ait.app.entity.Order;
import com.ait.app.entity.OrderItem;
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
import com.ait.app.dto.OrderRequestDTO;
import com.ait.app.dto.OrderResponseDTO;
import com.ait.app.dto.OrderValidationItemDTO;
import com.ait.app.dto.OrderValidationRequestDTO;
import com.ait.app.dto.OrderValidationResponseDTO;
import com.ait.app.entity.Address;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.entity.MenuItem;
import com.ait.app.enums.PaymentStatus;
import com.ait.app.exception.OrderCancellationException;
import com.ait.app.repository.AddressRepository;
import com.ait.app.repository.CartItemRepository;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.MenuItemRepository;
import com.ait.app.service.CartService;
import com.ait.app.service.OrderValidationService;


@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderValidationService orderValidationService;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private CartService cartService;

	@Override
	public OrderResponseDTO cancelOrder(Integer orderId, Integer userId) {
		throw new UnsupportedOperationException("Cancel order not implemented yet");
	}

	@Override
	@Transactional
	public OrderResponseDTO createOrder(OrderRequestDTO request) {

		Cart cart = cartRepository.findByUserId(request.getUserId()).orElse(null);

		if (cart == null) {
			throw new ResourceNotFoundException("Cart not found for user");
		}

		if (cart.getRestaurantId() == null) {
			throw new InvalidRequestException("Cart has no restaurant");
		}

		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

		if (cartItems == null || cartItems.isEmpty()) {
			throw new InvalidRequestException("Cart is empty");
		}

		Optional<Address> addressOptional = addressRepository.findByIdAndUserId(request.getAddressId(),
				request.getUserId());

		if (addressOptional.isEmpty()) {
			throw new ResourceNotFoundException("Address not found for user");
		}

		Address address = addressOptional.get();

		OrderValidationRequestDTO validationRequest = createValidationRequest(request.getUserId(),
				cart.getRestaurantId(), cartItems);

		OrderValidationResponseDTO validateResponse = orderValidationService.validateOrder(validationRequest);

		if (!validateResponse.isValid()) {
			throw new InvalidRequestException(buildErrorMessage(validateResponse.getErrors()));
		}

		Order order = new Order();
		order.setUserId(request.getUserId());
		order.setRestaurantId(cart.getRestaurantId());
		order.setDeliveryAddressSnapshot(buildAddressSnapshot(address));
		order.setPaymentMethod(request.getPaymentMethod());
		order.setStatus(OrderStatus.PLACED);
		order.setPaymentStatus(PaymentStatus.PENDING);

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (CartItem cartItem : cartItems) {

			Integer menuItemId = cartItem.getMenuItemId().intValue();

			Optional<MenuItem> menuItemOptional = menuItemRepository.findById(menuItemId);

			if (menuItemOptional.isEmpty()) {
				throw new ResourceNotFoundException("Menu item not found with id: " + menuItemId);
			}

			MenuItem menuItem = menuItemOptional.get();

			OrderItem orderItem = new OrderItem(menuItemId, menuItem.getName(), cartItem.getUnitPrice(),
					cartItem.getQuantity());

			order.addOrderItem(orderItem);

			totalAmount = totalAmount.add(orderItem.getSubtotal());
		}

		order.setTotalAmount(totalAmount);

		Order savedOrder = orderRepository.save(order);

		cartService.clearCart(request.getUserId());

		return convertToResponse(savedOrder);
	}

	private OrderValidationRequestDTO createValidationRequest(Integer userId, Integer restaurantId,
			List<CartItem> cartItems) {

		OrderValidationRequestDTO validationRequest = new OrderValidationRequestDTO();
		validationRequest.setUserId(userId);
		validationRequest.setRestaurantId(restaurantId);

		List<OrderValidationItemDTO> validationItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {

			OrderValidationItemDTO item = new OrderValidationItemDTO();
			item.setMenuItemId(cartItem.getMenuItemId().intValue());
			item.setQuantity(cartItem.getQuantity());
			item.setUnitPrice(cartItem.getUnitPrice());
			validationItems.add(item);
		}

		validationRequest.setItems(validationItems);

		return validationRequest;
	}

	private String buildAddressSnapshot(Address address) {

		StringBuilder snapshot = new StringBuilder();

		if (address.getStreetAddress() != null && !address.getStreetAddress().isBlank()) {
			snapshot.append(address.getStreetAddress());
		}

		if (address.getApartment() != null && !address.getApartment().isBlank()) {
			if (snapshot.length() > 0) {
				snapshot.append(", ");
			}
			snapshot.append(address.getApartment());
		}

		if (address.getLandmark() != null && !address.getLandmark().isBlank()) {
			if (snapshot.length() > 0) {
				snapshot.append(", ");
			}
			snapshot.append(address.getLandmark());
		}

		if (address.getCity() != null && !address.getCity().isBlank()) {
			if (snapshot.length() > 0) {
				snapshot.append(", ");
			}
			snapshot.append(address.getCity());
		}

		if (address.getPostalCode() != null && !address.getPostalCode().isBlank()) {
			if (snapshot.length() > 0) {
				snapshot.append(", ");
			}
			snapshot.append(address.getPostalCode());
		}

		if (address.getDeliveryInstructions() != null && !address.getDeliveryInstructions().isBlank()) {
			if (snapshot.length() > 0) {
				snapshot.append(" | ");
			}
			snapshot.append(address.getDeliveryInstructions());
		}

		if (snapshot.length() == 0) {
			throw new InvalidRequestException("Delivery address is incomplete");
		}

		return snapshot.toString();
	}

	private String buildErrorMessage(List<String> errors) {

		if (errors == null || errors.isEmpty()) {
			return "Order validation failed";
		}

		StringBuilder message = new StringBuilder();

		for (int i = 0; i < errors.size(); i++) {
			if (i > 0) {
				message.append(", ");
			}
			message.append(errors.get(i));
		}

		return message.toString();
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
		response.setPaymentMethod(order.getPaymentMethod());
		response.setCreatedAt(order.getCreatedAt());
		response.setUpdatedAt(order.getUpdatedAt());

		return response;
	}

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

	@Override
	public OrderDetailDTO getOrderById(Integer orderId, Integer userId, String userRole) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		OrderAuthorizationHelper.verifyOrderViewAuthorization(order, userId, userRole, restaurantRepository);

		return convertToDetailDTO(order);
	}

	private OrderDetailDTO convertToDetailDTO(Order order) {
		OrderDetailDTO dto = new OrderDetailDTO();
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

		List<OrderItemDetailDTO> itemDTOs = order.getOrderItems().stream()
				.map(this::convertToOrderItemDetailDTO)
				.collect(Collectors.toList());
		dto.setOrderItems(itemDTOs);

		List<OrderStatusHistoryDTO> historyDTOs = order.getStatusHistory().stream()
				.map(this::convertToStatusHistoryDTO)
				.collect(Collectors.toList());
		dto.setStatusHistory(historyDTOs);

		return dto;
	}

	private OrderItemDetailDTO convertToOrderItemDetailDTO(OrderItem item) {
		OrderItemDetailDTO dto = new OrderItemDetailDTO();
		dto.setId(item.getId());
		dto.setMenuItemId(item.getMenuItemId());
		dto.setItemNameSnapshot(item.getItemNameSnapshot());
		dto.setUnitPrice(item.getUnitPrice());
		dto.setQuantity(item.getQuantity());
		dto.setSubtotal(item.getSubtotal());
		return dto;
	}

	private OrderStatusHistoryDTO convertToStatusHistoryDTO(OrderStatusHistory history) {
		OrderStatusHistoryDTO dto = new OrderStatusHistoryDTO();
		dto.setId(history.getId());
		dto.setFromStatus(history.getFromStatus());
		dto.setToStatus(history.getToStatus());
		dto.setChangedAt(history.getChangedAt());
		dto.setChangedBy(history.getChangedBy());
		return dto;
	}
}
