package com.ait.app.service;

import com.ait.app.dto.CartRequestDTO;
import com.ait.app.dto.CartResponseDTO;

public interface CartService {

	CartResponseDTO createCart(CartRequestDTO request);

	CartResponseDTO getMyCart(Integer userId);

	void clearCart(Integer userId);

	void deleteCartItem(Integer itemId, Integer userId);
}
