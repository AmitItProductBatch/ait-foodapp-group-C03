package com.ait.app.serviceimpl;

import com.ait.app.repository.MenuItemRepository;
import com.ait.app.entity.MenuItem;
import java.math.RoundingMode;
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

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Override
	public CartItemResponseDTO createCartItem(CartItemRequestDTO dto, Integer userId) {

		Cart cart = cartRepository.findById(dto.getCartId()).orElse(null);

		if (cart == null || !userId.equals(cart.getUserId())) {
			throw new ResourceNotFoundException("Cart not found");
		}

		MenuItem menuItem = menuItemRepository.findById(dto.getMenuItemId().intValue()).orElse(null);

		if (menuItem == null || Boolean.TRUE.equals(menuItem.getDeleted())) {
			throw new ResourceNotFoundException("Menu item not found");
		}

		if (!Boolean.TRUE.equals(menuItem.getAvailability()) || menuItem.getPrice() == null) {
			throw new InvalidRequestException("Menu item is not available");
		}

		BigDecimal unitPrice = BigDecimal.valueOf(menuItem.getPrice()).setScale(2, RoundingMode.HALF_UP);

		Integer menuRestaurantId = menuItem.getRestaurant().getId();

		if (cart.getRestaurantId() != null && !cart.getRestaurantId().equals(menuRestaurantId)
				&& !cartItemRepository.findByCartId(cart.getId()).isEmpty()) {
			throw new InvalidRequestException(
					"Your cart has items from another restaurant. Clear the cart before adding items from this one.");
		}

		cart.setRestaurantId(menuRestaurantId);

		CartItem existingItem = cartItemRepository.findByCartIdAndMenuItemId(dto.getCartId(), dto.getMenuItemId())
				.orElse(null);

		if (existingItem != null) {
			throw new ResourceAlreadyExistsException("Menu item already exists in cart");
		}
		CartItem item = new CartItem();

		item.setCart(cart);
		item.setMenuItemId(dto.getMenuItemId());
		item.setQuantity(dto.getQuantity());
		item.setUnitPrice(unitPrice);

		BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));

		item.setSubtotal(subtotal);

		CartItem saved = cartItemRepository.save(item);

		double newTotal = 0.0;

		for (CartItem cartItem : cartItemRepository.findByCartId(cart.getId())) {
			newTotal += cartItem.getSubtotal().doubleValue();
		}

		cart.setTotalAmount(newTotal);
		cartRepository.save(cart);

		return toResponse(saved);
	}

	private CartItemResponseDTO toResponse(CartItem item) {

		String name = menuItemRepository.findById(item.getMenuItemId().intValue()).map(MenuItem::getName).orElse(null);

		CartItemResponseDTO response = new CartItemResponseDTO(item.getMenuItemId(), name,
				item.getUnitPrice().doubleValue(), item.getQuantity(), item.getSubtotal().doubleValue());

		response.setCartItemId(item.getId());

		return response;
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
			CartItemResponseDTO itemResponse = toResponse(item);
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