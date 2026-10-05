package com.ait.app.dto;

import java.util.List;

public class OrderValidationRequestDTO {
	private Integer userId;
	private Integer restaurantId;
	private List<OrderValidationItemDTO> items;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public List<OrderValidationItemDTO> getItems() {
		return items;
	}

	public void setItems(List<OrderValidationItemDTO> items) {
		this.items = items;
	}

}
