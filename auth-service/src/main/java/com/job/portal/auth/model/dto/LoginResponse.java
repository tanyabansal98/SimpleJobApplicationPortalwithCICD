package com.job.portal.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LoginResponse is a Data Transfer Object (DTO).
 * Analogy: A receipt or badge. After logging in, the server prints a receipt showing
 * the JWT token and basic user details, which the frontend saves in memory.
 *
 * @Data: Lombok annotation that generates Getters, Setters, etc.
 * @AllArgsConstructor: Lombok annotation that generates a constructor with all fields.
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    // The generated JWT token that the frontend will include in future API requests.
    private String token;
    // The user's email address.
    private String email;
    // The user's role (e.g. STUDENT), used by the frontend to decide which dashboard to show.
    private String role;
    // The database ID of the user.
    private Long userId;
}
