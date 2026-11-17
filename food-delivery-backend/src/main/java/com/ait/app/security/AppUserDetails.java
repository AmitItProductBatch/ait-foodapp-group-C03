package com.ait.app.security;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ait.app.entity.User;

public class AppUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final int id;
	private final String email;
	private final String password;
	private final String role;
	private final boolean active;

	public AppUserDetails(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.role = user.getRole() == null ? "USER" : user.getRole().trim().toUpperCase(Locale.ROOT);
		this.active = user.getActive() != null && user.getActive();
	}

	public int getId() {
		return id;
	}

	public String getRole() {
		return role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role));
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}
}
