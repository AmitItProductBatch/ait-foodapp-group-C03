package com.ait.app.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.CartItemRequestDTO;
import com.ait.app.dto.CartItemResponseDTO;
import com.ait.app.dto.CartResponseDTO;
import com.ait.app.dto.UpdateQuantityDTO;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceAlreadyExistsException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.repository.CartItemRepository;
import com.ait.app.repository.CartRepository;
import com.ait.app.service.CartItemService;

@Service
public class CartItemServiceimpl implements CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartRepository cartRepository;

	@Override
	public CartItemResponseDTO createCartItem(CartItemRequestDTO dto) {

		Cart cart = cartRepository.findById(dto.getCartId()).orElse(null);

		if (cart == null) {throw new ResourceNotFoundException("Cart not found");}

		CartItem existingItem = cartItemRepository.findByCartIdAndMenuItemId(dto.getCartId(), dto.getMenuItemId()).orElse(null);

		if (existingItem != null) {
			throw new ResourceAlreadyExistsException("Menu item already exists in cart");
		}
		CartItem item = new CartItem();

		item.setCart(cart);
		item.setMenuItemId(dto.getMenuItemId());
		item.setQuantity(dto.getQuantity());
		item.setUnitPrice(dto.getUnitPrice());

		BigDecimal subtotal = dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));

		item.setSubtotal(subtotal);

		CartItem saved = cartItemRepository.save(item);

		return new CartItemResponseDTO(
				saved.getMenuItemId(),
				null,
				saved.getUnitPrice().doubleValue(),
				saved.getQuantity(),
				saved.getSubtotal().doubleValue());
	}

	@Override
	@Transactional
	public CartResponseDTO updateCartItemQuantity(Integer itemId, Integer userId, UpdateQuantityDTO dto) {
		Integer quantity = dto.getQuantity();

		if (quantity < 0) {
			throw new InvalidRequestException("Quantity cannot be negative");
		}

		Optional<CartItem> cartItemOptional = cartItemRepository.findByIdAndCartUserId(itemId, userId);

		if (cartItemOptional.isEmpty()) {
			throw new ResourceNotFoundException("Cart item not found or does not belong to user");
		}

		CartItem cartItem = cartItemOptional.get();
		Cart cart = cartItem.getCart();

		if (quantity == 0) {
			cartItemRepository.delete(cartItem);
		} else {
			cartItem.setQuantity(quantity);
			cartItem.calculateSubtotal();
			cartItemRepository.save(cartItem);
		}

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

		List<CartItemResponseDTO> itemResponses = new ArrayList<>();

		for (CartItem item : remainingItems) {
			CartItemResponseDTO itemResponse = new CartItemResponseDTO(
					item.getMenuItemId(),
					null,
					item.getUnitPrice().doubleValue(),
					item.getQuantity(),
					item.getSubtotal().doubleValue()
			);
			itemResponses.add(itemResponse);
		}

		CartResponseDTO response = new CartResponseDTO();
		response.setId(cart.getId());
		response.setUserId(cart.getUserId());
		response.setRestaurantId(cart.getRestaurantId());
		response.setItems(itemResponses);
		response.setTotalAmount(cart.getTotalAmount());
		response.setCreatedAt(cart.getCreatedAt());
		response.setUpdatedAt(cart.getUpdatedAt());

		return response;
	}
}