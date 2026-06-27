package com.job.portal.auth.service;

import com.job.portal.auth.exception.AuthException;
import com.job.portal.auth.jwt.JwtUtil;
import com.job.portal.auth.model.User;
import com.job.portal.auth.model.dto.LoginRequest;
import com.job.portal.auth.model.dto.LoginResponse;
import com.job.portal.auth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService represents the Service layer.
 * Analogy: The brain of the application. The Service layer contains all our business logic 
 * rules (e.g., verifying passwords, checking account status, issuing tokens). It does not 
 * handle HTTP details (Controller does that) and does not talk directly to SQL (Repository does that).
 *
 * @Service: Tells Spring that this is a business service component.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Constructor Injection: Spring will automatically pass the required dependencies here.
    public AuthService(UserRepository userRepository, 
                       BCryptPasswordEncoder passwordEncoder, 
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user and returns a JWT token.
     * 
     * @param request The login DTO containing email and password
     * @return The login response DTO containing token and user details
     * @throws AuthException if login fails (invalid credentials or inactive account)
     */
    public LoginResponse login(LoginRequest request) {
        // a) Find user by email — throw a clear exception if not found.
        // For security reasons, we throw a general "Invalid email or password" error.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        // b) Check user.getActive() — throw if deactivated.
        // We throw a specific deactivated message as requested.
        if (Boolean.FALSE.equals(user.getActive())) {
            throw new AuthException("User account is deactivated");
        }

        // c) BCrypt.matches(rawPassword, hashedFromDB) — throw if wrong.
        // Compares the raw incoming password with the BCrypt hash from the database.
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Invalid email or password");
        }

        // d) Generate JWT via JwtUtil
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getUserId());

        // e) Return LoginResponse DTO
        return new LoginResponse(token, user.getEmail(), user.getRole(), user.getUserId());
    }
}
