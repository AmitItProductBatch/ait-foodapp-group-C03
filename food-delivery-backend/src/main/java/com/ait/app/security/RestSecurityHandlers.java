package com.ait.app.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.ait.app.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestSecurityHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	public RestSecurityHandlers(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		Object reason = request.getAttribute(JwtFilter.AUTH_ERROR_ATTRIBUTE);

		String message = reason != null ? reason.toString() : "Authentication required. Please log in.";

		write(request, response, HttpStatus.UNAUTHORIZED, message);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {

		write(request, response, HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
	}

	private void write(HttpServletRequest request, HttpServletResponse response, HttpStatus status, String message)
			throws IOException {

		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message,
				request.getRequestURI());

		objectMapper.writeValue(response.getOutputStream(), body);
	}
}
