package com.job.portal.exception;

// Thrown when a requested item (like a specific Job ID) cannot be found in the database.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
