package com.job.portal.controller;

import com.job.portal.model.User;
import com.job.portal.model.enums.Role;
import com.job.portal.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web Layer Tests for AuthController (main backend app).
 *
 * We use @WebMvcTest to load only the web layer — no DB, no real services.
 *
 * @TestPropertySource overrides the auth-service URL so that any attempt to
 * contact the real microservice will time out instantly, letting us test the
 * fallback behavior cleanly without waiting for real network timeouts.
 */
import org.springframework.context.annotation.Import;
import com.job.portal.config.SecurityConfig;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        // Point to a port that nothing is listening on → connection refused → fast fallback
        "app.auth-service.url=http://localhost:19999"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock the UserService so we control login/fallback outcomes without a real DB.
    @MockitoBean
    private UserService userService;

    // A fully set-up student user that represents a valid DB record.
    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setUserId(3L);
        validUser.setEmail("student@northeastern.edu");
        validUser.setRole(Role.STUDENT);
        validUser.setActive(true);
    }

    /**
     * TEST 5 — Login Falls Back to Local DB When Microservice Is Down (Resilience Test)
     *
     * SCENARIO: The auth-service microservice is unreachable (bad URL/port above).
     *           The student submits the login form with valid credentials.
     * EXPECTED: The backend falls back to local DB auth, login succeeds,
     *           and the response redirects to /dashboard.
     *
     * This test verifies your resilience logic in AuthController.tryMicroserviceLogin()
     * and the fallback call to userService.login().
     */
    @Test
    @DisplayName("Test 5: Auth-service down → fallback to local DB → redirect to /dashboard")
    void whenMicroserviceDown_thenFallbackToDB_andRedirectToDashboard() throws Exception {
        // ARRANGE: The fallback (local DB) will succeed and return our valid student.
        // userService.login() is what gets called when the microservice is unreachable.
        when(userService.login("student@northeastern.edu", "password123"))
                .thenReturn(validUser);

        // ACT & ASSERT: Submit the login form.
        // The microservice call will fail (no server at port 19999), triggering the fallback.
        mockMvc.perform(post("/login")
                        .param("email", "student@northeastern.edu")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())        // HTTP 302 redirect
                .andExpect(redirectedUrl("/dashboard"));       // Goes to the dashboard
    }

    /**
     * TEST 6 — Wrong Credentials Show Error on Login Page (Error Feedback Test)
     *
     * SCENARIO: A user submits the login form with credentials that don't match any account.
     *           Both the microservice AND the local DB reject the login.
     * EXPECTED: The user stays on the login page (index view) and the page shows an error message.
     */
    @Test
    @DisplayName("Test 6: Wrong credentials → stays on login page with error message")
    void whenWrongCredentials_thenStayOnLoginPageWithError() throws Exception {
        // ARRANGE: Simulate both auth paths failing.
        // userService.login() is the final fallback — if this throws, login fails completely.
        when(userService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Invalid email or password"));

        // ACT & ASSERT: Submit the login form with bad credentials.
        mockMvc.perform(post("/login")
                        .param("email", "wrong@email.com")
                        .param("password", "badpassword"))
                .andExpect(status().isOk())           // HTTP 200 (page re-rendered, not a redirect)
                .andExpect(view().name("index"))       // Stays on the login page
                .andExpect(model().attributeExists("error")); // Error message is set in the model
    }
}
