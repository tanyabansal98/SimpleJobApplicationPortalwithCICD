package com.job.portal.auth.controller;

import com.job.portal.auth.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * JwtTestController is a temporary test window. It allows us to manually trigger
 * our JWT generation and validation methods using our web browser or tools like Postman/curl.

 */
@RestController
public class JwtTestController {

    // Inject our JwtUtil tool that we built.
    private final JwtUtil jwtUtil;

    // Spring will automatically pass the JwtUtil bean here.
    public JwtTestController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint to generate a token.
     *
     * @param email User email
     * @param role User role (e.g. STUDENT)
     * @param userId User database ID
     * @return The raw JWT string
     */
    @GetMapping("/jwt-test/generate")
    public String generate(
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam Long userId) {
        // We call our jwtUtil tool to build and sign the token.
        return jwtUtil.generateToken(email, role, userId);
    }

    /**
     * Endpoint to validate and decode a token
     *
     * @param token The JWT string to validate
     * @return A map containing whether it's valid, and if so, the decoded claims
     */
    @GetMapping("/jwt-test/validate")
    public Map<String, Object> validate(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();

        // 1. Verify if the token is authentic and not expired
        boolean isValid = jwtUtil.validateToken(token);
        response.put("valid", isValid);

        if (isValid) {
            // 2. If it is valid, extract the claims (printed details) inside it
            Claims claims = jwtUtil.extractClaims(token);

            // 3. Put the claims into the JSON response
            response.put("email", claims.getSubject());
            response.put("role", claims.get("role", String.class));
            response.put("userId", claims.get("userId", Long.class));
        }

        return response;
    }
}
