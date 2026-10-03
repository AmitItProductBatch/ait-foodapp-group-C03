
package com.ait.app.service;

import java.time.LocalDateTime;

import com.ait.app.dto.OrderHistoryResponseDTO;
import com.ait.app.dto.OrderRequestDTO;
import com.ait.app.dto.OrderResponseDTO;


public interface OrderService {


	OrderResponseDTO createOrder(OrderRequestDTO request);

	OrderResponseDTO cancelOrder(Integer orderId, Integer userId);

	OrderHistoryResponseDTO getUserOrderHistory(Integer userId, String status, LocalDateTime fromDate,
			LocalDateTime toDate, int page, int size);

}