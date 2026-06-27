package com.job.portal.auth.exception;

/**
 * AuthException represents custom business-logic security failures.
 * Analogy: A specific alarm flag in a security building. When someone enters the wrong password, 
 * we trigger a specific "Authentication Failed" alarm rather than a generic system error.
 */
public class AuthException extends RuntimeException {
    
    // Pass the error message up to the parent RuntimeException class.
    public AuthException(String message) {
        super(message);
    }
}
