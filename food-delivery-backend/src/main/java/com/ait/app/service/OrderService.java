package com.ait.app.service;

import com.ait.app.dto.OrderRequestDTO;
import com.ait.app.dto.OrderResponseDTO;

public interface OrderService {

	OrderResponseDTO createOrder(OrderRequestDTO request);
	
}