package com.ait.app.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ait.app.util.LogMaskingUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
	private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
	private static final String CORRELATION_ID = "correlationId";
	private static final List<String> EXCLUDED_PATHS = Arrays.asList(
		"/actuator/health",
		"/actuator/info"
	);
	private static final List<String> SENSITIVE_HEADERS = Arrays.asList(
		"authorization",
		"cookie",
		"x-api-key"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		if (isExcludedPath(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String correlationId = getOrGenerateCorrelationId(request);
		MDC.put(CORRELATION_ID, correlationId);
		response.setHeader(CORRELATION_ID_HEADER, correlationId);

		long startTime = System.currentTimeMillis();
		String method = request.getMethod();
		String uri = request.getRequestURI();

		try {
			filterChain.doFilter(request, response);
		} finally {
			long duration = System.currentTimeMillis() - startTime;
			int status = response.getStatus();

			logger.info("{} {} - Status: {} - Duration: {}ms - CorrelationId: {}", method, uri, status, duration,
					correlationId);

			MDC.remove(CORRELATION_ID);
		}
	}

	private boolean isSensitiveHeader(String headerName) {
		return SENSITIVE_HEADERS.stream().anyMatch(sensitive -> headerName.toLowerCase().contains(sensitive));
	}

	private String getOrGenerateCorrelationId(HttpServletRequest request) {
		String correlationId = request.getHeader(CORRELATION_ID_HEADER);
		if (correlationId == null || correlationId.isBlank()) {
			correlationId = UUID.randomUUID().toString();
		}
		return correlationId;
	}

	private boolean isExcludedPath(String path) {
		return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
	}
}
