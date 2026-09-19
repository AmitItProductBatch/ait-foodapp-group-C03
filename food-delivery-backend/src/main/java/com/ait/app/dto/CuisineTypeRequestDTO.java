package com.ait.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CuisineTypeRequestDTO {

	@NotBlank(message = "Cuisine name is required")
	@Size(max = 100, message = "Cuisine name must not exceed 100 characters")
	private String name;

	@NotBlank(message = "Cuisine description is required")
	@Size(max = 500, message = "Cuisine description must not exceed 500 characters")
	private String description;

	public CuisineTypeRequestDTO() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}