package com.ait.app.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.CartItemResponseDTO;
import com.ait.app.dto.CartRequestDTO;
import com.ait.app.dto.CartResponseDTO;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.repository.CartItemRepository;
import com.ait.app.repository.CartRepository;
import com.ait.app.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Override
	public CartResponseDTO createCart(CartRequestDTO request) {

		if (cartRepository.existsByUserId(request.getUserId())) {

			throw new RuntimeException("Cart already exists for user ID: " + request.getUserId());
		}

		Cart cart = new Cart();

		cart.setUserId(request.getUserId());
		cart.setRestaurantId(request.getRestaurantId());
		cart.setTotalAmount(0.0);

		Cart savedCart = cartRepository.save(cart);

		CartResponseDTO response = new CartResponseDTO();

		response.setId(savedCart.getId());
		response.setUserId(savedCart.getUserId());
		response.setRestaurantId(savedCart.getRestaurantId());
		response.setTotalAmount(savedCart.getTotalAmount());
		response.setCreatedAt(savedCart.getCreatedAt());
		response.setUpdatedAt(savedCart.getUpdatedAt());
		response.setItems(new ArrayList<>());

		return response;
	}

	@Override
	public CartResponseDTO getMyCart(Integer userId) {

		Cart cart = cartRepository.findByUserId(userId).orElse(null);

		if (cart == null) {

			CartResponseDTO response = new CartResponseDTO();

			response.setId(null);
			response.setUserId(userId);
			response.setRestaurantId(null);
			response.setItems(new ArrayList<>());
			response.setTotalAmount(0.0);

			return response;
		}

		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

		List<CartItemResponseDTO> itemResponses = new ArrayList<>();

		double totalAmount = 0.0;

		for (CartItem cartItem : cartItems) {

			Long itemId = cartItem.getMenuItemId();

			Double unitPrice = cartItem.getUnitPrice().doubleValue();

			Integer quantity = cartItem.getQuantity();

			Double subtotal = cartItem.getSubtotal().doubleValue();

			CartItemResponseDTO itemResponse = new CartItemResponseDTO(itemId, null, unitPrice, quantity, subtotal);

			itemResponses.add(itemResponse);

			totalAmount += subtotal;
		}

		CartResponseDTO response = new CartResponseDTO();

		response.setId(cart.getId());
		response.setUserId(cart.getUserId());
		response.setRestaurantId(cart.getRestaurantId());
		response.setItems(itemResponses);
		response.setTotalAmount(totalAmount);
		response.setCreatedAt(cart.getCreatedAt());
		response.setUpdatedAt(cart.getUpdatedAt());

		return response;
	}

	@Override
	public void clearCart(Integer userId) {

		Cart cart = cartRepository.findByUserId(userId).orElse(null);

		if (cart == null) {
			return;
		}

		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

		for (CartItem cartItem : cartItems) {
			cartItemRepository.delete(cartItem);
		}

		cart.setRestaurantId(null);
		cart.setTotalAmount(0.0);
		cartRepository.save(cart);
	}

	@Override
	public void deleteCartItem(Integer itemId, Integer userId) {

		Optional<CartItem> cartItemOptional = cartItemRepository.findByIdAndCartUserId(itemId, userId);

		if (cartItemOptional.isEmpty()) {
			throw new ResourceNotFoundException("Cart item not found or does not belong to user");
		}

		CartItem cartItem = cartItemOptional.get();
		Cart cart = cartItem.getCart();

		cartItemRepository.delete(cartItem);

		List<CartItem> remainingItems = cartItemRepository.findByCartId(cart.getId());

		double newTotal = 0.0;

		for (CartItem item : remainingItems) {
			newTotal += item.getSubtotal().doubleValue();
		}

		cart.setTotalAmount(newTotal);

		if (remainingItems.isEmpty()) {
			cart.setRestaurantId(null);
		}

		cartRepository.save(cart);
	}
}
