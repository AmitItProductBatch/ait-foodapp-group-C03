package com.ait.app.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final int MIN_SECRET_BYTES = 32;

	private final SecretKey key;
	private final long expirationMs;

	public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms:3600000}") long expirationMs) {

		byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);

		if (bytes.length < MIN_SECRET_BYTES) {
			throw new IllegalStateException("jwt.secret must be at least " + MIN_SECRET_BYTES + " bytes long");
		}

		this.key = Keys.hmacShaKeyFor(bytes);
		this.expirationMs = expirationMs;
	}

	public String generateToken(int userId, String email, String role) {

		long now = System.currentTimeMillis();

		return Jwts.builder().subject(email).claim("uid", userId).claim("role", role).issuedAt(new Date(now))
				.expiration(new Date(now + expirationMs)).signWith(key).compact();
	}

	public Claims extractClaims(String token) {

		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}

	public long getExpirationMs() {
		return expirationMs;
	}
}
