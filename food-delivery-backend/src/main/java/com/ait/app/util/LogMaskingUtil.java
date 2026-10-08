package com.ait.app.util;

public class LogMaskingUtil {

	private static final String MASK = "*****";

	public static String maskEmail(String email) {
		if (email == null || email.isBlank()) {
			return email;
		}
		int atIndex = email.indexOf('@');
		if (atIndex <= 0) {
			return MASK;
		}
		String username = email.substring(0, atIndex);
		if (username.length() <= 2) {
			return MASK + email.substring(atIndex);
		}
		return username.substring(0, 2) + MASK + email.substring(atIndex);
	}

	public static String maskPhoneNumber(String phone) {
		if (phone == null || phone.isBlank()) {
			return phone;
		}
		String digits = phone.replaceAll("[^0-9]", "");
		if (digits.length() <= 4) {
			return MASK;
		}
		return digits.substring(0, 2) + MASK + digits.substring(digits.length() - 2);
	}

	public static String maskAddress(String address) {
		if (address == null || address.isBlank()) {
			return address;
		}
		if (address.length() <= 10) {
			return MASK;
		}
		return address.substring(0, 5) + MASK + address.substring(address.length() - 5);
	}

	public static String maskToken(String token) {
		if (token == null || token.isBlank()) {
			return token;
		}
		if (token.length() <= 8) {
			return MASK;
		}
		return token.substring(0, 4) + MASK + token.substring(token.length() - 4);
	}

	public static String maskPassword(String password) {
		if (password == null) {
			return password;
		}
		return MASK;
	}

	public static String maskAuthorizationHeader(String authHeader) {
		if (authHeader == null || authHeader.isBlank()) {
			return authHeader;
		}
		if (authHeader.length() <= 8) {
			return MASK;
		}
		return authHeader.substring(0, 8) + MASK;
	}
}
