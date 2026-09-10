package com.ait.app.service;


import com.ait.app.dto.MenuItemRequestDTO;
import com.ait.app.dto.MenuItemResponseDTO;

public interface MenuItemService {

    MenuItemResponseDTO createMenuItem(
            int restaurantId,
            MenuItemRequestDTO requestDTO
    );
}