package com.job.portal.exception;

// Thrown when an entity (like a user email) already exists in the system.
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
