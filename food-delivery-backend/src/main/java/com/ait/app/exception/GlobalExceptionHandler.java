package com.ait.app.exception;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ait.app.logging.CorrelationIdFilter;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred. Please try again later.";

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {

		return buildExpectedResponse(HttpStatus.NOT_FOUND, ex, request);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex,
			WebRequest request) {

		return buildExpectedResponse(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex, WebRequest request) {

		return buildExpectedResponse(HttpStatus.BAD_REQUEST, ex, request);
	}

	@ExceptionHandler(UnauthorizedActionException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedAction(UnauthorizedActionException ex, WebRequest request) {

		return buildExpectedResponse(HttpStatus.FORBIDDEN, ex, request);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest request) {

		return buildExpectedResponse(HttpStatus.UNAUTHORIZED, ex, request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {

		return buildExpectedResponse(HttpStatus.FORBIDDEN,
				new UnauthorizedActionException("You do not have permission to perform this action"), request);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, WebRequest request) {

		return buildExpectedResponse(HttpStatus.UNAUTHORIZED,
				new InvalidCredentialsException("Authentication required. Please log in."), request);
	}

	@ExceptionHandler(OrderCancellationException.class)
	public ResponseEntity<ErrorResponse> handlerOrderCancellation(OrderCancellationException ex, WebRequest request) {

		return buildExpectedResponse(HttpStatus.CONFLICT, ex, request);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			WebRequest request) {

		return buildExpectedResponse(HttpStatus.BAD_REQUEST, ex, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());

		logExpected(HttpStatus.BAD_REQUEST, "Validation failed " + fieldErrors, request);

		ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Validation failed", path(request), fieldErrors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		logExpected(HttpStatus.BAD_REQUEST, "Malformed JSON request body", request);

		ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Malformed JSON request body", path(request));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {

		if (statusCode.is5xxServerError()) {
			logUnexpected(ex, request);
		} else {
			logExpected(statusCode, ex.getClass().getSimpleName() + ": " + ex.getMessage(), request);
		}

		return super.handleExceptionInternal(ex, body, headers, statusCode, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAnyOtherException(Exception ex, WebRequest request) {

		logUnexpected(ex, request);

		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE, request);
	}

	private ResponseEntity<ErrorResponse> buildExpectedResponse(HttpStatus status, Exception ex, WebRequest request) {

		logExpected(status, ex.getMessage(), request);

		return buildResponse(status, ex.getMessage(), request);
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, WebRequest request) {

		ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message, path(request));

		return ResponseEntity.status(status).body(body);
	}

	private void logExpected(HttpStatusCode status, String detail, WebRequest request) {

		log.warn("Request failed: status={} path={} detail={}", status.value(), path(request), detail);
	}

	private void logUnexpected(Exception ex, WebRequest request) {

		log.error("Unexpected error: correlationId={} path={}", correlationId(), path(request), ex);
	}

	private String correlationId() {

		String id = MDC.get(CorrelationIdFilter.MDC_KEY);

		return id != null ? id : UUID.randomUUID().toString();
	}

	private String path(WebRequest request) {

		String description = request.getDescription(false);

		if (description.startsWith("uri=")) {
			return description.substring(4);
		}

		return description;
	}
}
