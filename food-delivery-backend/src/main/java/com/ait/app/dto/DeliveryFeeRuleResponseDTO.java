package com.ait.app.dto;

import java.time.LocalDateTime;

public class DeliveryFeeRuleResponseDTO {

	private int id;
	private Double baseFee;
	private Double perKmRate;
	private Double maxDeliveryRadius;
	private Double freeDeliveryThreshold;
	private Boolean active;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public DeliveryFeeRuleResponseDTO() {
	}

	public DeliveryFeeRuleResponseDTO(int id, Double baseFee, Double perKmRate, Double maxDeliveryRadius,
			Double freeDeliveryThreshold, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.baseFee = baseFee;
		this.perKmRate = perKmRate;
		this.maxDeliveryRadius = maxDeliveryRadius;
		this.freeDeliveryThreshold = freeDeliveryThreshold;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getBaseFee() {
		return baseFee;
	}

	public void setBaseFee(Double baseFee) {
		this.baseFee = baseFee;
	}

	public Double getPerKmRate() {
		return perKmRate;
	}

	public void setPerKmRate(Double perKmRate) {
		this.perKmRate = perKmRate;
	}

	public Double getMaxDeliveryRadius() {
		return maxDeliveryRadius;
	}

	public void setMaxDeliveryRadius(Double maxDeliveryRadius) {
		this.maxDeliveryRadius = maxDeliveryRadius;
	}

	public Double getFreeDeliveryThreshold() {
		return freeDeliveryThreshold;
	}

	public void setFreeDeliveryThreshold(Double freeDeliveryThreshold) {
		this.freeDeliveryThreshold = freeDeliveryThreshold;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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
}
