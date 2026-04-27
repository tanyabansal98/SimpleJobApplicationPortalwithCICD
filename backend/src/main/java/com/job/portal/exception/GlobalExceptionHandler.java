package com.job.portal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Global Exception Handler
 *
 * Centralizes error handling for all controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    // Handles cases where a requested item is missing (404 Not Found).
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    // Handles cases where something already exists, like a duplicate email (409 Conflict).
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicate(DuplicateResourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }

    // Handles cases where a logic rule is broken, like an illegal status change (400 Bad Request).
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<?> handleInvalidTransition(InvalidStatusTransitionException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
    }

    // Handles generic bad inputs or validation failures (400 Bad Request).
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
    }

    // The ultimate safety net for any other unhandled errors (500 Internal Server Error).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "An unexpected error occurred."));
    }
}
