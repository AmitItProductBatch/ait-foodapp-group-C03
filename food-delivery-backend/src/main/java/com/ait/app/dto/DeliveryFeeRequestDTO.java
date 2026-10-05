package com.ait.app.dto;

import jakarta.validation.constraints.NotNull;

public class DeliveryFeeRequestDTO {

	@NotNull(message = "Restaurant ID is required")
	private Integer restaurantId;

	@NotNull(message = "Delivery address ID is required")
	private Integer addressId;

	public DeliveryFeeRequestDTO() {
	}

	public DeliveryFeeRequestDTO(Integer restaurantId, Integer addressId) {
		this.restaurantId = restaurantId;
		this.addressId = addressId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
}
