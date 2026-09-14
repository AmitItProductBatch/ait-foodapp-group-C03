package com.ait.app.exception;

/**
 * Thrown when a request violates a business rule that isn't a simple bean
 * validation failure (missing owner id, price <= 0, inactive account, ...).
 * Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class InvalidRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidRequestException(String message) {
        super(message);
    }
}
