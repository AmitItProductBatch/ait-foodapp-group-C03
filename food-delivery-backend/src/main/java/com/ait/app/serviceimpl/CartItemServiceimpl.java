package com.ait.app.serviceimpl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ait.app.dto.CartItemRequestDTO;
import com.ait.app.dto.CartItemResponseDTO;
import com.ait.app.entity.Cart;
import com.ait.app.entity.CartItem;
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
}