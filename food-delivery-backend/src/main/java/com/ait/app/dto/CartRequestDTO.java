package com.ait.app.dto;

import jakarta.validation.constraints.NotNull;

public class CartRequestDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    private Integer restaurantId;

    public CartRequestDTO() {
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
}