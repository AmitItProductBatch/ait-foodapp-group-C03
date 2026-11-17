package com.ait.app.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	public static final String AUTH_ERROR_ATTRIBUTE = "auth.error";

	private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
	private static final String BEARER = "Bearer ";

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;

	public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith(BEARER)) {
			chain.doFilter(request, response);
			return;
		}

		String token = header.substring(BEARER.length()).trim();

		try {
			String email = jwtUtil.extractUsername(token);

			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails user = userDetailsService.loadUserByUsername(email);

				if (!user.isEnabled()) {
					request.setAttribute(AUTH_ERROR_ATTRIBUTE, "User account is deactivated");
				} else {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
							null, user.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}

		} catch (ExpiredJwtException e) {
			request.setAttribute(AUTH_ERROR_ATTRIBUTE, "Token has expired");
		} catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
			log.warn("Rejected bearer token: {}", e.getClass().getSimpleName());
			request.setAttribute(AUTH_ERROR_ATTRIBUTE, "Invalid token");
		}

		chain.doFilter(request, response);
	}
}
