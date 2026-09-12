package com.ait.app.service;

import com.ait.app.dto.MenuItemRequestDTO;
import com.ait.app.dto.MenuItemResponseDTO;
import com.ait.app.dto.PriceResponseDTO;

public interface MenuItemService {

	MenuItemResponseDTO createMenuItem(int restaurantId, MenuItemRequestDTO requestDTO);

	PriceResponseDTO getItemPrice(int itemId);
}
