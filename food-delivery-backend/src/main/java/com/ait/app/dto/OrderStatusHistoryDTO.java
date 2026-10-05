package com.ait.app.dto;

import java.time.LocalDateTime;

import com.ait.app.enums.OrderStatus;

public class OrderStatusHistoryDTO {

	private Integer id;
	private OrderStatus fromStatus;
	private OrderStatus toStatus;
	private LocalDateTime changedAt;
	private Integer changedBy;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public OrderStatus getFromStatus() {
		return fromStatus;
	}

	public void setFromStatus(OrderStatus fromStatus) {
		this.fromStatus = fromStatus;
	}

	public OrderStatus getToStatus() {
		return toStatus;
	}

	public void setToStatus(OrderStatus toStatus) {
		this.toStatus = toStatus;
	}

	public LocalDateTime getChangedAt() {
		return changedAt;
	}

	public void setChangedAt(LocalDateTime changedAt) {
		this.changedAt = changedAt;
	}

	public Integer getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(Integer changedBy) {
		this.changedBy = changedBy;
	}
}
