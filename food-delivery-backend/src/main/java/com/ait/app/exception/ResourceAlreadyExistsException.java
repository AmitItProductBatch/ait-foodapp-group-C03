package com.ait.app.exception;

/**
 * Thrown when an operation would create a duplicate entity (email already
 * registered, cart already exists for user, role name already exists, menu
 * item already in cart, ...). Mapped to HTTP 409 by {@link GlobalExceptionHandler}.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
