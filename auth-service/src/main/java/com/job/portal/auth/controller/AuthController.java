package com.job.portal.auth.controller;

import com.job.portal.auth.model.dto.LoginRequest;
import com.job.portal.auth.model.dto.LoginResponse;
import com.job.portal.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @RequestMapping("/auth"): All URLs in this class will start with /auth (e.g. /auth/login).
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login endpoint.
     * Accessible at: POST http://localhost:9090/auth/login
     * 
     * @param loginRequest The JSON payload containing email and password
     * @return The LoginResponse payload (token, user details)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // Delegate the business validation logic to our AuthService
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
