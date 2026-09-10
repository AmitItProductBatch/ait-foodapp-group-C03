package com.ait.app.dto;

public class RestaurantResponseDTO {

    private int restaurantId;
    private int ownerId;
    private String message;
    private String status;


    public RestaurantResponseDTO() {
    }


    public RestaurantResponseDTO(int restaurantId,int ownerId,String message,String status) {

        this.restaurantId = restaurantId;
        this.ownerId = ownerId;
        this.message = message;
        this.status = status;
    }


    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }


    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}