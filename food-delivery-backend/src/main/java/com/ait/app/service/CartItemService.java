package com.ait.app.service;

import com.ait.app.dto.CartItemRequestDTO;
import com.ait.app.dto.CartItemResponseDTO;

public interface CartItemService {

	
    CartItemResponseDTO createCartItem(CartItemRequestDTO dto);
}