/*
package com.ait.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;


public class RestaurantRequestDTO {

   @NotBlank(message = "Restaurant name is required")
	private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Cuisine is required")
    private String cuisine;

    @NotBlank(message = "Contact is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Contact must be exactly 10 digits"
    )
    private String contact;

    @NotNull(message = "Owner ID is required")
    private Integer ownerId;

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

	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
    
    

}
*/
package com.ait.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RestaurantRequestDTO {

    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Cuisine is required")
    private String cuisine;

    @NotBlank(message = "Contact is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Contact must be exactly 10 digits"
    )
    private String contact;

    @NotBlank(message = "Hours are required")
    private String hours;

    @NotNull(message = "Owner ID is required")
    private Integer ownerId;

    public RestaurantRequestDTO() {
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

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
}