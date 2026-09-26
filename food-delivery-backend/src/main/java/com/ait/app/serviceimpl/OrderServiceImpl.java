package com.ait.app.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.OrderRequestDTO;
import com.ait.app.dto.OrderResponseDTO;
import com.ait.app.dto.OrderValidationItemDTO;
import com.ait.app.dto.OrderValidationRequestDTO;
import com.ait.app.dto.OrderValidationResponseDTO;
import com.ait.app.entity.Address;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.entity.MenuItem;
import com.ait.app.entity.Order;
import com.ait.app.entity.OrderItem;
import com.ait.app.enums.OrderStatus;
import com.ait.app.enums.PaymentStatus;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.repository.AddressRepository;
import com.ait.app.repository.CartItemRepository;
import com.ait.app.repository.CartRepository;
import com.ait.app.repository.MenuItemRepository;
import com.ait.app.repository.OrderRepository;
import com.ait.app.service.CartService;
import com.ait.app.service.OrderService;
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
}
