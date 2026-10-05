package com.ait.app.dto;

import java.math.BigDecimal;

public class OrderItemDetailDTO {

	private Integer id;
	private Integer menuItemId;
	private String itemNameSnapshot;
	private BigDecimal unitPrice;
	private Integer quantity;
	private BigDecimal subtotal;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
}
