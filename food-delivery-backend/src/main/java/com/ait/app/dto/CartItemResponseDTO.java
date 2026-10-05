package com.ait.app.dto;

public class CartItemResponseDTO {

	private Long itemId;

	private String itemName;

	private Double unitPrice;

	private Integer quantity;

	private Double subtotal;

	public CartItemResponseDTO() {
	}

	public CartItemResponseDTO(Long itemId, String itemName, Double unitPrice, Integer quantity, Double subtotal) {

		this.itemId = itemId;
		this.itemName = itemName;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.subtotal = subtotal;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}
}