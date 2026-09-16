package com.ait.app.service;

import com.ait.app.dto.CartRequestDTO;
import com.ait.app.dto.CartResponseDTO;

public interface CartService {

	CartResponseDTO createCart(CartRequestDTO request);

	void clearCart(Integer userId);
}