package com.ait.app.dto;

public class UpdateProfileDto {

	private String name;
	private String phonenumber;
	private AdressUpdateDto adressUpdateDto;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public AdressUpdateDto getAdressUpdateDto() {
		return adressUpdateDto;
	}
	public void setAdressUpdateDto(AdressUpdateDto adressUpdateDto) {
		this.adressUpdateDto = adressUpdateDto;
	}
	
}
