package com.ait.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ait.app.entity.DeliveryFeeRule;
import com.ait.app.repository.DeliveryFeeRuleRepository;

@SpringBootTest
class DeliveryFeeRuleRepositoryIntegrationTest {

	@Autowired
	private DeliveryFeeRuleRepository deliveryFeeRuleRepository;

	@BeforeEach
	void setUp() {
		deliveryFeeRuleRepository.deleteAll();
	}

	@Test
	void shouldPersistDeliveryFeeRule() {
		DeliveryFeeRule rule = new DeliveryFeeRule();
		rule.setBaseFee(5.0);
		rule.setPerKmRate(1.5);
		rule.setMaxDeliveryRadius(10.0);
		rule.setFreeDeliveryThreshold(50.0);
		rule.setActive(true);
		rule.setCreatedAt(LocalDateTime.now());
		rule.setUpdatedAt(LocalDateTime.now());

		DeliveryFeeRule savedRule = deliveryFeeRuleRepository.saveAndFlush(rule);

		assertThat(savedRule.getId()).isNotNull();
		assertThat(savedRule.getBaseFee()).isEqualTo(5.0);
		assertThat(savedRule.getPerKmRate()).isEqualTo(1.5);
		assertThat(savedRule.getMaxDeliveryRadius()).isEqualTo(10.0);
		assertThat(savedRule.getFreeDeliveryThreshold()).isEqualTo(50.0);
		assertThat(savedRule.getActive()).isTrue();

		var fetchedRule = deliveryFeeRuleRepository.findById(savedRule.getId());

		assertThat(fetchedRule).isPresent();
		assertThat(fetchedRule.get().getBaseFee()).isEqualTo(5.0);
	}

	@Test
	void shouldFindActiveRuleOrderByUpdatedAtDesc() {
		DeliveryFeeRule rule1 = new DeliveryFeeRule();
		rule1.setBaseFee(3.0);
		rule1.setPerKmRate(1.0);
		rule1.setMaxDeliveryRadius(8.0);
		rule1.setFreeDeliveryThreshold(40.0);
		rule1.setActive(true);
		rule1.setCreatedAt(LocalDateTime.now().minusDays(2));
		rule1.setUpdatedAt(LocalDateTime.now().minusDays(2));

		DeliveryFeeRule rule2 = new DeliveryFeeRule();
		rule2.setBaseFee(5.0);
		rule2.setPerKmRate(1.5);
		rule2.setMaxDeliveryRadius(10.0);
		rule2.setFreeDeliveryThreshold(50.0);
		rule2.setActive(true);
		rule2.setCreatedAt(LocalDateTime.now().minusDays(1));
		rule2.setUpdatedAt(LocalDateTime.now().minusDays(1));

		deliveryFeeRuleRepository.saveAndFlush(rule1);
		deliveryFeeRuleRepository.saveAndFlush(rule2);

		List<DeliveryFeeRule> activeRules = deliveryFeeRuleRepository.findAllActiveOrderByUpdatedAtDesc();

		assertThat(activeRules).isNotEmpty();
		assertThat(activeRules.get(0).getBaseFee()).isEqualTo(5.0);
		assertThat(activeRules.get(0).getUpdatedAt()).isAfter(rule1.getUpdatedAt());
	}

	@Test
	void shouldReturnEmptyWhenNoActiveRuleExists() {
		DeliveryFeeRule rule = new DeliveryFeeRule();
		rule.setBaseFee(5.0);
		rule.setPerKmRate(1.5);
		rule.setMaxDeliveryRadius(10.0);
		rule.setFreeDeliveryThreshold(50.0);
		rule.setActive(false);
		rule.setCreatedAt(LocalDateTime.now());
		rule.setUpdatedAt(LocalDateTime.now());

		deliveryFeeRuleRepository.saveAndFlush(rule);

		List<DeliveryFeeRule> activeRules = deliveryFeeRuleRepository.findAllActiveOrderByUpdatedAtDesc();

		assertThat(activeRules).isEmpty();
	}
}
