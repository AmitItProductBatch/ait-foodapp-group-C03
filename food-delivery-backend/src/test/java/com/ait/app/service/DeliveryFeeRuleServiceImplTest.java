package com.ait.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ait.app.dto.DeliveryFeeRuleRequestDTO;
import com.ait.app.dto.DeliveryFeeRuleResponseDTO;
import com.ait.app.entity.DeliveryFeeRule;
import com.ait.app.entity.User;
import com.ait.app.exception.InvalidRequestException;
import com.ait.app.exception.ResourceNotFoundException;
import com.ait.app.exception.UnauthorizedActionException;
import com.ait.app.repository.DeliveryFeeRuleRepository;
import com.ait.app.repository.UserRepository;
import com.ait.app.serviceimpl.DeliveryFeeRuleServiceImpl;

@ExtendWith(MockitoExtension.class)
class DeliveryFeeRuleServiceImplTest {

	@Mock
	private DeliveryFeeRuleRepository deliveryFeeRuleRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private DeliveryFeeRuleServiceImpl deliveryFeeRuleService;

	private DeliveryFeeRuleRequestDTO requestDTO;
	private User adminUser;
	private DeliveryFeeRule existingRule;
	private DeliveryFeeRule newRule;

	@BeforeEach
	void setUp() {
		requestDTO = new DeliveryFeeRuleRequestDTO();
		requestDTO.setBaseFee(5.0);
		requestDTO.setPerKmRate(1.5);
		requestDTO.setMaxDeliveryRadius(10.0);
		requestDTO.setFreeDeliveryThreshold(50.0);

		adminUser = new User();
		adminUser.setId(1);
		adminUser.setName("Admin User");
		adminUser.setEmail("admin@example.com");
		adminUser.setRole("ADMIN");
		adminUser.setActive(true);

		existingRule = new DeliveryFeeRule();
		existingRule.setId(1);
		existingRule.setBaseFee(3.0);
		existingRule.setPerKmRate(1.0);
		existingRule.setMaxDeliveryRadius(8.0);
		existingRule.setFreeDeliveryThreshold(40.0);
		existingRule.setActive(true);
		existingRule.setCreatedAt(LocalDateTime.now().minusDays(1));
		existingRule.setUpdatedAt(LocalDateTime.now().minusDays(1));

		newRule = new DeliveryFeeRule();
		newRule.setId(2);
		newRule.setBaseFee(5.0);
		newRule.setPerKmRate(1.5);
		newRule.setMaxDeliveryRadius(10.0);
		newRule.setFreeDeliveryThreshold(50.0);
		newRule.setActive(true);
		newRule.setCreatedAt(LocalDateTime.now());
		newRule.setUpdatedAt(LocalDateTime.now());
	}

	@Test
	void shouldUpdateDeliveryRulesSuccessfullyWithExistingActiveRule() {
		when(userRepository.findById(1)).thenReturn(Optional.of(adminUser));
		when(deliveryFeeRuleRepository.findAllActiveOrderByUpdatedAtDesc()).thenReturn(List.of(existingRule));
		when(deliveryFeeRuleRepository.save(any(DeliveryFeeRule.class))).thenReturn(newRule);

		DeliveryFeeRuleResponseDTO response = deliveryFeeRuleService.updateDeliveryRules(requestDTO, 1);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(2);
		assertThat(response.getBaseFee()).isEqualTo(5.0);
		assertThat(response.getPerKmRate()).isEqualTo(1.5);
		assertThat(response.getMaxDeliveryRadius()).isEqualTo(10.0);
		assertThat(response.getFreeDeliveryThreshold()).isEqualTo(50.0);
		assertThat(response.getActive()).isTrue();

		verify(userRepository).findById(1);
		verify(deliveryFeeRuleRepository).findAllActiveOrderByUpdatedAtDesc();
		verify(deliveryFeeRuleRepository, times(2)).save(any(DeliveryFeeRule.class));
	}

	@Test
	void shouldUpdateDeliveryRulesSuccessfullyWithoutExistingActiveRule() {
		when(userRepository.findById(1)).thenReturn(Optional.of(adminUser));
		when(deliveryFeeRuleRepository.findAllActiveOrderByUpdatedAtDesc()).thenReturn(List.of());
		when(deliveryFeeRuleRepository.save(any(DeliveryFeeRule.class))).thenReturn(newRule);

		DeliveryFeeRuleResponseDTO response = deliveryFeeRuleService.updateDeliveryRules(requestDTO, 1);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(2);
		assertThat(response.getBaseFee()).isEqualTo(5.0);
		assertThat(response.getPerKmRate()).isEqualTo(1.5);
		assertThat(response.getMaxDeliveryRadius()).isEqualTo(10.0);
		assertThat(response.getFreeDeliveryThreshold()).isEqualTo(50.0);
		assertThat(response.getActive()).isTrue();

		verify(userRepository).findById(1);
		verify(deliveryFeeRuleRepository).findAllActiveOrderByUpdatedAtDesc();
		verify(deliveryFeeRuleRepository).save(any(DeliveryFeeRule.class));
	}

	@Test
	void shouldThrowUnauthorizedWhenNonAdminTriesToUpdate() {
		User regularUser = new User();
		regularUser.setId(2);
		regularUser.setRole("CUSTOMER");
		regularUser.setActive(true);

		when(userRepository.findById(2)).thenReturn(Optional.of(regularUser));

		assertThatThrownBy(() -> deliveryFeeRuleService.updateDeliveryRules(requestDTO, 2))
				.isInstanceOf(UnauthorizedActionException.class).hasMessageContaining("Only admin users can update delivery rules");

		verify(userRepository).findById(2);
		verify(deliveryFeeRuleRepository, never()).save(any(DeliveryFeeRule.class));
	}

	@Test
	void shouldThrowInvalidRequestWhenInactiveAdminTriesToUpdate() {
		adminUser.setActive(false);

		when(userRepository.findById(1)).thenReturn(Optional.of(adminUser));

		assertThatThrownBy(() -> deliveryFeeRuleService.updateDeliveryRules(requestDTO, 1))
				.isInstanceOf(InvalidRequestException.class).hasMessageContaining("Admin account is not active");

		verify(userRepository).findById(1);
		verify(deliveryFeeRuleRepository, never()).save(any(DeliveryFeeRule.class));
	}

	@Test
	void shouldThrowResourceNotFoundWhenUserDoesNotExist() {
		when(userRepository.findById(999)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> deliveryFeeRuleService.updateDeliveryRules(requestDTO, 999))
				.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("User");

		verify(userRepository).findById(999);
		verify(deliveryFeeRuleRepository, never()).save(any(DeliveryFeeRule.class));
	}

	@Test
	void shouldGetActiveDeliveryRulesSuccessfully() {
		when(deliveryFeeRuleRepository.findAllActiveOrderByUpdatedAtDesc()).thenReturn(List.of(existingRule));

		DeliveryFeeRuleResponseDTO response = deliveryFeeRuleService.getActiveDeliveryRules();

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(1);
		assertThat(response.getBaseFee()).isEqualTo(3.0);
		assertThat(response.getPerKmRate()).isEqualTo(1.0);
		assertThat(response.getMaxDeliveryRadius()).isEqualTo(8.0);
		assertThat(response.getFreeDeliveryThreshold()).isEqualTo(40.0);
		assertThat(response.getActive()).isTrue();

		verify(deliveryFeeRuleRepository).findAllActiveOrderByUpdatedAtDesc();
	}

	@Test
	void shouldThrowResourceNotFoundWhenNoActiveRulesExist() {
		when(deliveryFeeRuleRepository.findAllActiveOrderByUpdatedAtDesc()).thenReturn(List.of());

		assertThatThrownBy(() -> deliveryFeeRuleService.getActiveDeliveryRules())
				.isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("No active delivery rules found");

		verify(deliveryFeeRuleRepository).findAllActiveOrderByUpdatedAtDesc();
	}
}
