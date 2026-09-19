package com.ait.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "categories", uniqueConstraints = { @UniqueConstraint(name = "uk_category_name", columnNames = "name") })
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Column(nullable = false, unique = true, length = 100)
	private String name;

	@Size(max = 500)
	@Column(length = 500)
	private String description;

	@Column(nullable = false)
	private boolean active = true;

	public Category() {
	}

	public Category(String name, String description) {
		this.name = name;
		this.description = description;
		this.active = true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}