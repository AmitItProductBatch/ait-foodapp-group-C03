package com.ait.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cuisine_types", uniqueConstraints = { @UniqueConstraint(columnNames = "name") })
public class CuisineType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Cuisine name is required")
	@Size(max = 100, message = "Cuisine name must not exceed 100 characters")
	@Column(nullable = false, unique = true)
	private String name;

	@NotBlank(message = "Cuisine description is required")
	@Size(max = 500, message = "Cuisine description must not exceed 500 characters")
	@Column(nullable = false, length = 500)
	private String description;

	public CuisineType() {
	}

	public CuisineType(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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