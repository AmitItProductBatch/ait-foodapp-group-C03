package com.ait.app.exception;

/**
 * Thrown on login when the email/password combination is invalid, or the
 * account is deactivated. Mapped to HTTP 401 by {@link GlobalExceptionHandler}.
 */
public class InvalidCredentialsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
