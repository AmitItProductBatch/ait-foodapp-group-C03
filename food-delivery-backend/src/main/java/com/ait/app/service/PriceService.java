package com.ait.app.service;

import com.ait.app.dto.PriceCalculationRequestDTO;
import com.ait.app.dto.PriceCalculationResponseDTO;

public interface PriceService {

	PriceCalculationResponseDTO calculatePrice(PriceCalculationRequestDTO request);
	
}