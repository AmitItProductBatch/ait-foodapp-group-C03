package com.ait.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ait.app.enums.OrderStatus;
import com.ait.app.enums.PaymentStatus;

public class OrderResponseDTO {

	private Integer id;
	private Integer userId;
	private Integer restaurantId;
	private String deliveryAddressSnapshot;
	private BigDecimal totalAmount;
	private OrderStatus status;
	private PaymentStatus paymentStatus;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<OrderItemRequestDTO> orderItems;

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

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
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

	public List<OrderItemRequestDTO> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemRequestDTO> orderItems) {
		this.orderItems = orderItems;
	}
}