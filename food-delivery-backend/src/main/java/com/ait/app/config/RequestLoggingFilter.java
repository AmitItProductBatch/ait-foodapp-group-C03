package com.ait.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

	private static final String CORRELATION_ID = "X-Correlation-Id";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String correlationId = request.getHeader(CORRELATION_ID);

		if (correlationId == null || correlationId.isBlank()) {
			correlationId = UUID.randomUUID().toString();
		}

		long startTime = System.currentTimeMillis();

		MDC.put(CORRELATION_ID, correlationId);

		response.setHeader(CORRELATION_ID, correlationId);

		try {

			filterChain.doFilter(request, response);

		} finally {

			long duration = System.currentTimeMillis() - startTime;

			log.info("Request completed | method={} | path={} | status={} | duration={}ms", request.getMethod(),
					request.getRequestURI(), response.getStatus(), duration);

			MDC.remove(CORRELATION_ID);
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {

		String path = request.getRequestURI();

		return path.equals("/actuator/health") || path.startsWith("/static/") || path.equals("/favicon.ico");
	}
}