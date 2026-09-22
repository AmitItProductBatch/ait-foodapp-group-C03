
package com.ait.app.service;

import com.ait.app.dto.DeliveryFeeRequestDTO;
import com.ait.app.dto.DeliveryFeeResponseDTO;
import com.ait.app.dto.OrderTotalResponseDTO;
import com.ait.app.dto.PriceCalculationRequestDTO;
import com.ait.app.dto.PriceCalculationResponseDTO;

public interface PriceService {

	PriceCalculationResponseDTO calculatePrice(PriceCalculationRequestDTO request);

	OrderTotalResponseDTO calculateOrderTotal(Integer userId);

	DeliveryFeeResponseDTO calculateDeliveryFee(DeliveryFeeRequestDTO request);

}
