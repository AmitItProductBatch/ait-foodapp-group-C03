package com.ait.app.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoleControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String validRoleJson;

	@BeforeEach
	void setUp() {
		validRoleJson = "{\"name\":\"ADMIN\",\"description\":\"Administrator role with full access to all system features\"}";
	}

	@Test
	void createRole_Success_Returns201() throws Exception {
		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validRoleJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("ADMIN"))
				.andExpect(jsonPath("$.description").value("Administrator role with full access to all system features"));
	}

	@Test
	void createRole_DuplicateName_Returns400() throws Exception {
		// First creation should succeed
		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validRoleJson))
				.andExpect(status().isCreated());

		// Second creation with same name should fail
		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validRoleJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createRole_MissingName_Returns400() throws Exception {
		String invalidJson = "{\"description\":\"Administrator role with full access\"}";

		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createRole_MissingDescription_Returns400() throws Exception {
		String invalidJson = "{\"name\":\"ADMIN\"}";

		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createRole_InvalidNameFormat_Lowercase_Returns400() throws Exception {
		String invalidJson = "{\"name\":\"admin\",\"description\":\"Administrator role with full access\"}";

		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createRole_InvalidNameFormat_SpecialCharacters_Returns400() throws Exception {
		String invalidJson = "{\"name\":\"ADMIN-ROLE\",\"description\":\"Administrator role with full access\"}";

		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createRole_NameTooShort_Returns400() throws Exception {
		String invalidJson = "{\"name\":\"A\",\"description\":\"Administrator role with full access\"}";

		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createRole_NameTooLong_Returns400() throws Exception {
		String invalidJson = "{\"name\":\"VERY_LONG_ROLE_NAME_THAT_EXCEEDS_MAXIMUM_ALLOWED_LENGTH\",\"description\":\"Administrator role with full access\"}";

		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createRole_DescriptionTooShort_Returns400() throws Exception {
		String invalidJson = "{\"name\":\"ADMIN\",\"description\":\"Short\"}";

		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createRole_ValidNameWithUnderscore_Returns201() throws Exception {
		String validJson = "{\"name\":\"SUPER_ADMIN\",\"description\":\"Super administrator role with elevated privileges\"}";

		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("SUPER_ADMIN"));
	}

	@Test
	void createRole_ValidNameWithNumbers_Returns201() throws Exception {
		String validJson = "{\"name\":\"ROLE_1\",\"description\":\"Test role with number in name\"}";

		mockMvc.perform(post("/api/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("ROLE_1"));
	}

}
