
package com.ait.app.service;

import java.time.LocalDateTime;

import com.ait.app.dto.OrderHistoryResponseDTO;

public interface OrderService {

	OrderHistoryResponseDTO getUserOrderHistory(Integer userId, String status, LocalDateTime fromDate,
			LocalDateTime toDate, int page, int size);
}