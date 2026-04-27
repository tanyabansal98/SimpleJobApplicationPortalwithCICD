package com.job.portal.exception;

// Thrown when a job application status change is illegal (e.g. going from Rejected to Applied).
public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
