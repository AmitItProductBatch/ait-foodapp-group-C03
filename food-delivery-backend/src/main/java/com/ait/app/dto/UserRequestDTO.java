package com.ait.app.dto;

import com.ait.app.util.LogMaskingUtil;

public class UserRequestDTO {

	private String name;
	private String email;
	private String password;
	private String phonenumber;
	private String role;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	@Override
	public String toString() {
		return "UserRequestDTO{" +
				"name='" + name + '\'' +
				", email='" + LogMaskingUtil.maskEmail(email) + '\'' +
				", password='" + LogMaskingUtil.maskPassword(password) + '\'' +
				", phonenumber='" + LogMaskingUtil.maskPhoneNumber(phonenumber) + '\'' +
				", role='" + role + '\'' +
				'}';
	}
}