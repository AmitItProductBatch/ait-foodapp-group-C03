package com.ait.app.service;

import com.ait.app.dto.CartItemRequestDTO;
import com.ait.app.dto.CartItemResponseDTO;
import com.ait.app.dto.CartResponseDTO;
import com.ait.app.dto.UpdateQuantityDTO;

public interface CartItemService {

    CartItemResponseDTO createCartItem(CartItemRequestDTO dto);

    CartResponseDTO updateCartItemQuantity(Integer itemId, Integer userId, UpdateQuantityDTO dto);
}