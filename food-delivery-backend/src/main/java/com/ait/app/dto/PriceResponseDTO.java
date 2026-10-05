package com.ait.app.dto;

public class PriceResponseDTO {
    private int itemId;
    private String itemName;
    private Double price;

    public PriceResponseDTO() {
    }

    public PriceResponseDTO(int itemId, String itemName, Double price) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
