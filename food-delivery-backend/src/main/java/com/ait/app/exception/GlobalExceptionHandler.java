package com.ait.app.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
		logger.warn("Resource not found: {} - CorrelationId: {}", ex.getMessage(), MDC.get("correlationId"));

		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex,
			WebRequest request) {
		logger.warn("Resource already exists: {} - CorrelationId: {}", ex.getMessage(), MDC.get("correlationId"));

		return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex, WebRequest request) {
		logger.warn("Invalid request: {} - CorrelationId: {}", ex.getMessage(), MDC.get("correlationId"));

		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@ExceptionHandler(UnauthorizedActionException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedAction(UnauthorizedActionException ex, WebRequest request) {
		logger.warn("Unauthorized action: {} - CorrelationId: {}", ex.getMessage(), MDC.get("correlationId"));

		return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest request) {
		logger.warn("Invalid credentials: {} - CorrelationId: {}", ex.getMessage(), MDC.get("correlationId"));

		return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());

		logger.warn("Validation failed: {} - CorrelationId: {}", fieldErrors, MDC.get("correlationId"));

		ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Validation failed", path(request), fieldErrors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			WebRequest request) {
		logger.warn("Constraint violation: {} - CorrelationId: {}", ex.getMessage(), MDC.get("correlationId"));

		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		logger.warn("Malformed JSON request body - CorrelationId: {}", MDC.get("correlationId"));

		ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Malformed JSON request body", path(request));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAnyOtherException(Exception ex, WebRequest request) {
		logger.error("Unexpected error: {} - CorrelationId: {}", ex.getMessage(), MDC.get("correlationId"), ex);

		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, WebRequest request) {

		ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message, path(request));

		return ResponseEntity.status(status).body(body);
	}

	private String path(WebRequest request) {

		String description = request.getDescription(false);

		if (description.startsWith("uri=")) {
			return description.substring(4);
		}

		return description;
	}

	@ExceptionHandler(OrderCancellationException.class)
	public ResponseEntity<ErrorResponse> handlerOrderCancellation(OrderCancellationException ex, WebRequest request) {
		logger.warn("Order cancellation failed: {} - CorrelationId: {}", ex.getMessage(), MDC.get("correlationId"));

		return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
	}
}