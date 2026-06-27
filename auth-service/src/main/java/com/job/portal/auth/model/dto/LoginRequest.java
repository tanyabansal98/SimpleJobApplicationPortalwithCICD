package com.job.portal.auth.model.dto;

import lombok.Data;

/**
 * LoginRequest is a Data Transfer Object (DTO).
 * Analogy: A custom shipping form. When a customer orders a package, they fill out
 * only the necessary fields (username and password). They don't send their entire identity profile.
 *
 * @Data: Lombok annotation that generates Getters, Setters, toString, equals, and hashCode.
 */
@Data
public class LoginRequest {
    // The email address typed in by the user on the login form.
    private String email;
    // The raw, plain-text password typed in by the user.
    private String password;
}
