package com.ait.app.dto;


public class RestaurantDetailsDTO {

    private int restaurantId;
    private String name;
    private String address;
    private String hours;
    private String cuisine;
    private Double rating;

    public RestaurantDetailsDTO(int restaurantId, String name, String address, String hours, String cuisine,
    		Double rating) {
    	super();
    	this.restaurantId = restaurantId;
    	this.name = name;
    	this.address = address;
    	this.hours = hours;
    	this.cuisine = cuisine;
    	this.rating = rating;
    	}
    public RestaurantDetailsDTO() {
    }

    public int getRestaurantId() {
        return restaurantId;
    }

	public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}