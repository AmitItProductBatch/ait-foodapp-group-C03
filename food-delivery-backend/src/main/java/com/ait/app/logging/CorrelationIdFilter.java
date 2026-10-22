package com.ait.app.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

	public static final String HEADER_NAME = "X-Correlation-Id";
	public static final String MDC_KEY = "correlationId";

	private static final int MAX_LENGTH = 64;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String correlationId = resolve(request.getHeader(HEADER_NAME));

		MDC.put(MDC_KEY, correlationId);
		response.setHeader(HEADER_NAME, correlationId);

		try {
			chain.doFilter(request, response);
		} finally {
			MDC.remove(MDC_KEY);
		}
	}

	private String resolve(String incoming) {

		if (incoming != null && !incoming.isBlank() && incoming.length() <= MAX_LENGTH
				&& incoming.matches("[A-Za-z0-9._-]+")) {
			return incoming;
		}

		return UUID.randomUUID().toString();
	}
}
