package com.ait.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldCreateCategorySuccessfully() throws Exception {

		String json = """
				{
				    "name": "Integration Appetizers",
				    "description": "Light dishes"
				}
				""";

		mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("integration appetizers"))
				.andExpect(jsonPath("$.description").value("Light dishes"));
	}

	@Test
	void shouldReturn400WhenNameIsMissing() throws Exception {

		String json = """
				{
				    "description": "Light dishes"
				}
				""";

		mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenNameIsBlank() throws Exception {

		String json = """
				{
				    "name": "",
				    "description": "Light dishes"
				}
				""";

		mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenDescriptionIsTooLong() throws Exception {

		String longDescription = "a".repeat(501);

		String json = """
				{
				    "name": "Long Description Category",
				    "description": "%s"
				}
				""".formatted(longDescription);

		mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}
}