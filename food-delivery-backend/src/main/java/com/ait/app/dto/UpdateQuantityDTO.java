package com.ait.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateQuantityDTO {

	@NotNull(message = "Quantity is required")
	@Min(value = 0, message = "Quantity must be zero or positive")
	private Integer quantity;

	public UpdateQuantityDTO() {
	}

	public UpdateQuantityDTO(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
