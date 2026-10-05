package com.ait.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class DeliveryFeeRuleRequestDTO {

	@NotNull(message = "Base fee is required")
	@DecimalMin(value = "0.01", message = "Base fee must be positive")
	private Double baseFee;

	@NotNull(message = "Per km rate is required")
	@DecimalMin(value = "0.01", message = "Per km rate must be positive")
	private Double perKmRate;

	@NotNull(message = "Max delivery radius is required")
	@DecimalMin(value = "0.1", message = "Max delivery radius must be positive")
	private Double maxDeliveryRadius;

	@NotNull(message = "Free delivery threshold is required")
	@DecimalMin(value = "0.0", message = "Free delivery threshold must be non-negative")
	private Double freeDeliveryThreshold;

	public DeliveryFeeRuleRequestDTO() {
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
}
