package com.ait.app.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CartResponseDTO {

	private Integer id;

	private Integer userId;

	private Integer restaurantId;

	private Double totalAmount;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private List<CartItemResponseDTO> items;

	public CartResponseDTO() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<CartItemResponseDTO> getItems() {
		return items;
	}

	public void setItems(List<CartItemResponseDTO> items) {
		this.items = items;
	}
}