package com.ait.app.dto;

public class MenuItemResponseDTO {

	private int itemId;
	private int restaurantId;
	private String name;
	private String description;
	private Double price;
	private Boolean availability;
	private String category;
	private String message;

	public MenuItemResponseDTO() {
	}

	public MenuItemResponseDTO(
			int itemId,
			int restaurantId,
			String name,
			String description,
			Double price,
			Boolean availability,
			String category,
			String message) {
		this.itemId = itemId;
		this.restaurantId = restaurantId;
		this.name = name;
		this.description = description;
		this.price = price;
		this.availability = availability;
		this.category = category;
		this.message = message;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getAvailability() {
		return availability;
	}

	public void setAvailability(Boolean availability) {
		this.availability = availability;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
