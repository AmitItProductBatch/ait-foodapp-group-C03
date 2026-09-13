package com.ait.app.service;

import com.ait.app.dto.MenuItemRequestDTO;
import com.ait.app.dto.MenuItemResponseDTO;
import com.ait.app.dto.MenuItemUpdateDTO;

public interface MenuItemService {

	MenuItemResponseDTO createMenuItem(int restaurantId, MenuItemRequestDTO requestDTO);

	MenuItemResponseDTO updateMenuItem(Integer itemId,MenuItemUpdateDTO updateDTO,Integer adminId);
}
