package com.job.portal.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler acts as a safety net.
 * Analogy: A central emergency department. Whenever any controller or service throws an exception,
 * it gets forwarded here. We translate those ugly Java stack trace errors into clean, readable JSON 
 * messages with correct HTTP status codes before returning them to the user/frontend.
 *
 * @RestControllerAdvice: Tells Spring to apply this advice to all controllers in the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom authentication business exceptions (e.g. wrong password, inactive user).
     * Returns HTTP Status 401 (Unauthorized).
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuthException(AuthException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handles any other unexpected system errors (e.g. database goes down).
     * Returns HTTP Status 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
