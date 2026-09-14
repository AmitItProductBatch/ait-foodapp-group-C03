package com.ait.app.exception;

/**
 * Thrown when a requested entity (User, Restaurant, Cart, Address, MenuItem,
 * Role, ...) cannot be found. Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(resourceName + " not found with id: " + identifier);
    }
}
