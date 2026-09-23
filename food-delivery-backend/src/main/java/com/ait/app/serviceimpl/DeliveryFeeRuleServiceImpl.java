package com.ait.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.app.dto.DeliveryFeeRuleRequestDTO;
import com.ait.app.dto.DeliveryFeeRuleResponseDTO;
import com.ait.app.entity.DeliveryFeeRule;
import com.ait.app.entity.User;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.exception.UnauthorizedActionException;
import com.ait.app.repository.DeliveryFeeRuleRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.service.DeliveryFeeRuleService;

@Service
public class DeliveryFeeRuleServiceImpl implements DeliveryFeeRuleService {

	@Autowired
	private DeliveryFeeRuleRepository deliveryFeeRuleRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public DeliveryFeeRuleResponseDTO updateDeliveryRules(DeliveryFeeRuleRequestDTO requestDTO, Integer adminUserId) {
		User adminUser = userRepository.findById(adminUserId)
				.orElseThrow(() -> new ResourceNotFoundException("User", adminUserId));

		if (!"ADMIN".equalsIgnoreCase(adminUser.getRole())) {
			throw new UnauthorizedActionException("Only admin users can update delivery rules");
		}

		if (!Boolean.TRUE.equals(adminUser.getActive())) {
			throw new InvalidRequestException("Admin account is not active");
		}

		List<DeliveryFeeRule> existingActiveRules = deliveryFeeRuleRepository.findAllActiveOrderByUpdatedAtDesc();

		for (DeliveryFeeRule rule : existingActiveRules) {
			rule.setActive(false);
			deliveryFeeRuleRepository.save(rule);
		}

		DeliveryFeeRule newRule = new DeliveryFeeRule();
		newRule.setBaseFee(requestDTO.getBaseFee());
		newRule.setPerKmRate(requestDTO.getPerKmRate());
		newRule.setMaxDeliveryRadius(requestDTO.getMaxDeliveryRadius());
		newRule.setFreeDeliveryThreshold(requestDTO.getFreeDeliveryThreshold());
		newRule.setActive(true);
		newRule.setCreatedAt(LocalDateTime.now());
		newRule.setUpdatedAt(LocalDateTime.now());

		DeliveryFeeRule savedRule = deliveryFeeRuleRepository.save(newRule);

		return new DeliveryFeeRuleResponseDTO(
				savedRule.getId(),
				savedRule.getBaseFee(),
				savedRule.getPerKmRate(),
				savedRule.getMaxDeliveryRadius(),
				savedRule.getFreeDeliveryThreshold(),
				savedRule.getActive(),
				savedRule.getCreatedAt(),
				savedRule.getUpdatedAt()
		);
	}

	@Override
	public DeliveryFeeRuleResponseDTO getActiveDeliveryRules() {
		List<DeliveryFeeRule> activeRules = deliveryFeeRuleRepository.findAllActiveOrderByUpdatedAtDesc();

		if (activeRules.isEmpty()) {
			throw new ResourceNotFoundException("No active delivery rules found");
		}

		DeliveryFeeRule rule = activeRules.get(0);

		return new DeliveryFeeRuleResponseDTO(
				rule.getId(),
				rule.getBaseFee(),
				rule.getPerKmRate(),
				rule.getMaxDeliveryRadius(),
				rule.getFreeDeliveryThreshold(),
				rule.getActive(),
				rule.getCreatedAt(),
				rule.getUpdatedAt()
		);
	}
}
