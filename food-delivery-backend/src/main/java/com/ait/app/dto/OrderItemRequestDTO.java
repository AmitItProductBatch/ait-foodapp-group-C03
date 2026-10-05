package com.ait.app.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderItemRequestDTO {

	@NotNull(message = "Menu item ID is required")
	private Integer menuItemId;

	@NotBlank(message = "Item name snapshot is required")
	private String itemNameSnapshot;

	@NotNull(message = "Unit price is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be non-negative")
	private BigDecimal unitPrice;

	@NotNull(message = "Quantity is required")
	@Positive(message = "Quantity must be at least 1")
	private Integer quantity;

	public Integer getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(Integer menuItemId) {
		this.menuItemId = menuItemId;
	}

	public String getItemNameSnapshot() {
		return itemNameSnapshot;
	}

	public void setItemNameSnapshot(String itemNameSnapshot) {
		this.itemNameSnapshot = itemNameSnapshot;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}