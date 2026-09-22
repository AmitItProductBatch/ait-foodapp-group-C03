package com.ait.app.dto;

import java.math.BigDecimal;

public class DeliveryFeeResponseDTO {

	private Integer restaurantId;
	private Integer addressId;
	private double distanceKm;
	private BigDecimal baseFee;
	private BigDecimal distanceFee;
	private BigDecimal deliveryFee;
	private boolean freeDeliveryApplied;

	public DeliveryFeeResponseDTO() {
	}

	public DeliveryFeeResponseDTO(Integer restaurantId, Integer addressId, double distanceKm, BigDecimal baseFee,
			BigDecimal distanceFee, BigDecimal deliveryFee, boolean freeDeliveryApplied) {
		this.restaurantId = restaurantId;
		this.addressId = addressId;
		this.distanceKm = distanceKm;
		this.baseFee = baseFee;
		this.distanceFee = distanceFee;
		this.deliveryFee = deliveryFee;
		this.freeDeliveryApplied = freeDeliveryApplied;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}

	public BigDecimal getBaseFee() {
		return baseFee;
	}

	public void setBaseFee(BigDecimal baseFee) {
		this.baseFee = baseFee;
	}

	public BigDecimal getDistanceFee() {
		return distanceFee;
	}

	public void setDistanceFee(BigDecimal distanceFee) {
		this.distanceFee = distanceFee;
	}

	public BigDecimal getDeliveryFee() {
		return deliveryFee;
	}

	public void setDeliveryFee(BigDecimal deliveryFee) {
		this.deliveryFee = deliveryFee;
	}

	public boolean isFreeDeliveryApplied() {
		return freeDeliveryApplied;
	}

	public void setFreeDeliveryApplied(boolean freeDeliveryApplied) {
		this.freeDeliveryApplied = freeDeliveryApplied;
	}
}
