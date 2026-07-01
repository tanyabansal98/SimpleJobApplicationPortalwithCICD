package com.job.portal.auth.service;

import com.job.portal.auth.exception.AuthException;
import com.job.portal.auth.jwt.JwtUtil;
import com.job.portal.auth.model.User;
import com.job.portal.auth.model.dto.LoginRequest;
import com.job.portal.auth.model.dto.LoginResponse;
import com.job.portal.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit Tests for the AuthService (auth-service microservice).
 *
 * We use @ExtendWith(MockitoExtension.class) instead of @SpringBootTest so
 * these tests run WITHOUT starting up a real application context or connecting
 * to any database. This makes them very fast and isolated.
 *
 * @Mock    -> Creates a fake/dummy version of the dependency.
 * @InjectMocks -> Creates a REAL AuthService, but injects the mocks above into it.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    // We use a REAL BCryptPasswordEncoder here — no mocking.
    // This makes Test 2 realistic: it actually checks that BCrypt rejects wrong passwords.
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    // A fake user that represents a valid active account in the database.
    private User validUser;

    // The raw plain-text password used to create the hash below.
    private static final String PLAIN_TEXT_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        // Create a real BCrypt encoder and hash our test password.
        passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(PLAIN_TEXT_PASSWORD);

        // Build the fake User object that UserRepository will return.
        validUser = new User();
        validUser.setUserId(1L);
        validUser.setEmail("student@northeastern.edu");
        validUser.setPasswordHash(hashedPassword);
        validUser.setRole("STUDENT");
        validUser.setActive(true);

        // Manually inject the real encoder (since @InjectMocks created AuthService
        // using the @Mock BCryptPasswordEncoder, we need to replace it with the real one).
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    /**
     * TEST 1 — Valid Login Returns a JWT Token (Happy Path)
     *
     * SCENARIO: A student provides the correct email and password.
     * EXPECTED: AuthService returns a LoginResponse containing a non-null JWT token,
     *           the correct email, and the correct role.
     */
    @Test
    @DisplayName("Test 1: Valid credentials → returns LoginResponse with JWT token")
    void whenValidCredentials_thenReturnLoginResponseWithToken() {
        // ARRANGE: Set up the mocks to simulate a normal successful login.
        LoginRequest request = new LoginRequest();
        request.setEmail("student@northeastern.edu");
        request.setPassword(PLAIN_TEXT_PASSWORD);

        // Tell the fake UserRepository: "when asked for this email, return our fake user"
        when(userRepository.findByEmail("student@northeastern.edu"))
                .thenReturn(Optional.of(validUser));

        // Tell the fake JwtUtil: "when asked to generate a token, return this fake token string"
        when(jwtUtil.generateToken("student@northeastern.edu", "STUDENT", 1L))
                .thenReturn("fake-jwt-token-abc123");

        // ACT: Call the real login method in AuthService.
        LoginResponse response = authService.login(request);

        // ASSERT: Verify the response has all the expected values.
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake-jwt-token-abc123");
        assertThat(response.getEmail()).isEqualTo("student@northeastern.edu");
        assertThat(response.getRole()).isEqualTo("STUDENT");
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    /**
     * TEST 2 — Wrong Password Throws AuthException (Security Guard Test)
     *
     * SCENARIO: Someone provides the correct email but the WRONG password.
     * EXPECTED: AuthService throws an AuthException with a generic security message.
     *           It must NOT reveal which field was wrong (email vs password).
     */
    @Test
    @DisplayName("Test 2: Wrong password → throws AuthException with correct message")
    void whenWrongPassword_thenThrowAuthException() {
        // ARRANGE: The user is found in the DB, but we'll send the wrong password.
        LoginRequest request = new LoginRequest();
        request.setEmail("student@northeastern.edu");
        request.setPassword("thisIsTheWrongPassword!");

        // UserRepository finds the user fine (correct email)...
        when(userRepository.findByEmail("student@northeastern.edu"))
                .thenReturn(Optional.of(validUser));

        // ACT & ASSERT: The login should fail and throw AuthException.
        // assertThatThrownBy is the clean way to test that an exception is thrown.
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessage("Invalid email or password");
    }
}
