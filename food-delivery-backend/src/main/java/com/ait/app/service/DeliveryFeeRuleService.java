package com.ait.app.service;

import com.ait.app.dto.DeliveryFeeRuleRequestDTO;
import com.ait.app.dto.DeliveryFeeRuleResponseDTO;

public interface DeliveryFeeRuleService {

	DeliveryFeeRuleResponseDTO updateDeliveryRules(DeliveryFeeRuleRequestDTO requestDTO, Integer adminUserId);

	DeliveryFeeRuleResponseDTO getActiveDeliveryRules();
}
