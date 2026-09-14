package com.ait.app.exception;



/**
 * Thrown when an authenticated user is not allowed to perform the requested
 * action (e.g. not a PARTNER, not the owner of the restaurant). Mapped to
 * HTTP 403 by {@link GlobalExceptionHandler}.
 */
public class UnauthorizedActionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
