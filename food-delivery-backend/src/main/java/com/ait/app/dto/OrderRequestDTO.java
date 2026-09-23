package com.ait.app.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderRequestDTO {

	@NotNull(message = "User ID is required")
	private Integer userId;

	@NotNull(message = "Restaurant ID is required")
	private Integer restaurantId;

	@NotBlank(message = "Delivery address is required")
	private String deliveryAddressSnapshot;

	@NotNull(message = "Total amount is required")
	@DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
	private BigDecimal totalAmount;

	@NotNull(message = "Order items are required")
	@Valid
	private List<OrderItemRequestDTO> orderItems;

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

	public String getDeliveryAddressSnapshot() {
		return deliveryAddressSnapshot;
	}

	public void setDeliveryAddressSnapshot(String deliveryAddressSnapshot) {
		this.deliveryAddressSnapshot = deliveryAddressSnapshot;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<OrderItemRequestDTO> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemRequestDTO> orderItems) {
		this.orderItems = orderItems;
	}
}