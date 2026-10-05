package com.ait.app.service;

import com.ait.app.dto.OrderValidationRequestDTO;
import com.ait.app.dto.OrderValidationResponseDTO;

public interface OrderValidationService {
	OrderValidationResponseDTO validateOrder(OrderValidationRequestDTO request);

}
