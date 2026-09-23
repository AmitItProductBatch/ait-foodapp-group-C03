package com.ait.app.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderItemRequestDTO {

	@NotNull(message = "Menu item ID is required")
	private Integer menuItemId;

	@NotBlank(message = "Item name is required")
	private String itemName;

	@NotNull(message = "Unit price is required")
	@DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
	private BigDecimal unitPrice;

	@NotNull(message = "Quantity is required")
	@Positive(message = "Quantity must be greater than 0")
	private Integer quantity;

	@NotNull(message = "Subtotal is required")
	@DecimalMin(value = "0.01", message = "Subtotal must be greater than 0")
	private BigDecimal subtotal;

	public Integer getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(Integer menuItemId) {
		this.menuItemId = menuItemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
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

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
}