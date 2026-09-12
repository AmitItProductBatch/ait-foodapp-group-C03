package com.ait.app.dto;

import java.util.List;

public class LoginResponseDTO {

    private int id;
    private String name;
    private String email;
    private String phonenumber;
    private String role;
    private boolean active;
    private List<AdressUpdateDto> addresses;

    public LoginResponseDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<AdressUpdateDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AdressUpdateDto> addresses) {
        this.addresses = addresses;
    }
}