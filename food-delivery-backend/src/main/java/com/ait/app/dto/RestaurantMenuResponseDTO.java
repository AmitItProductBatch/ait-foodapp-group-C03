package com.ait.app.dto;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuResponseDTO {

	private int restaurantId;
	private List<MenuCategoryDTO> categories = new ArrayList<>();

	public RestaurantMenuResponseDTO() {
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public List<MenuCategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<MenuCategoryDTO> categories) {
		this.categories = categories;
	}
}
