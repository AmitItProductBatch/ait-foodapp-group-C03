package com.ait.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ait.app.entity.DeliveryFeeRule;
import com.ait.app.entity.User;
import com.ait.app.repository.DeliveryFeeRuleRepository;
import com.ait.app.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerDeliveryRulesIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DeliveryFeeRuleRepository deliveryFeeRuleRepository;

	private Integer adminUserId;
	private Integer customerUserId;

	@BeforeEach
	void setUp() {
		deliveryFeeRuleRepository.deleteAll();
		userRepository.deleteAll();

		User adminUser = new User();
		adminUser.setName("Test Admin");
		adminUser.setEmail("admin@test.com");
		adminUser.setPassword("password");
		adminUser.setPhonenumber("1234567890");
		adminUser.setRole("ADMIN");
		adminUser.setActive(true);
		User savedAdmin = userRepository.save(adminUser);
		adminUserId = savedAdmin.getId();

		User regularUser = new User();
		regularUser.setName("Test Customer");
		regularUser.setEmail("customer@test.com");
		regularUser.setPassword("password");
		regularUser.setPhonenumber("0987654321");
		regularUser.setRole("CUSTOMER");
		regularUser.setActive(true);
		User savedCustomer = userRepository.save(regularUser);
		customerUserId = savedCustomer.getId();
	}

	@Test
	void shouldUpdateDeliveryRulesSuccessfully() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.baseFee").value(5.0))
				.andExpect(jsonPath("$.perKmRate").value(1.5)).andExpect(jsonPath("$.maxDeliveryRadius").value(10.0))
				.andExpect(jsonPath("$.freeDeliveryThreshold").value(50.0)).andExpect(jsonPath("$.active").value(true));
	}

	@Test
	void shouldReturn400WhenBaseFeeIsMissing() throws Exception {
		String json = """
				{
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenBaseFeeIsZero() throws Exception {
		String json = """
				{
				    "baseFee": 0.0,
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenBaseFeeIsNegative() throws Exception {
		String json = """
				{
				    "baseFee": -5.0,
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenPerKmRateIsMissing() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenPerKmRateIsZero() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "perKmRate": 0.0,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenMaxDeliveryRadiusIsMissing() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "perKmRate": 1.5,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenMaxDeliveryRadiusIsZero() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 0.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenFreeDeliveryThresholdIsMissing() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 10.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenFreeDeliveryThresholdIsNegative() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": -10.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn404WhenNonExistentUserTriesToUpdate() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", "999").contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isNotFound());
	}

	@Test
	void shouldReturn403WhenNonAdminTriesToUpdate() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", customerUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isForbidden());
	}

	@Test
	void shouldGetActiveDeliveryRulesSuccessfully() throws Exception {
		String json = """
				{
				    "baseFee": 5.0,
				    "perKmRate": 1.5,
				    "maxDeliveryRadius": 10.0,
				    "freeDeliveryThreshold": 50.0
				}
				""";

		mockMvc.perform(put("/api/prices/delivery-rules").param("adminUserId", adminUserId.toString())
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());

		mockMvc.perform(get("/api/prices/delivery-rules").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.baseFee").exists())
				.andExpect(jsonPath("$.perKmRate").exists()).andExpect(jsonPath("$.maxDeliveryRadius").exists())
				.andExpect(jsonPath("$.freeDeliveryThreshold").exists()).andExpect(jsonPath("$.active").value(true));
	}

	@Test
	void shouldReturn404WhenNoActiveRulesExist() throws Exception {
		deliveryFeeRuleRepository.deleteAll();

		mockMvc.perform(get("/api/prices/delivery-rules").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}
