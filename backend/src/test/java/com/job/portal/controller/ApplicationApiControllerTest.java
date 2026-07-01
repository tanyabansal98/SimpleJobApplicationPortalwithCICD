package com.job.portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.portal.model.Application;
import com.job.portal.model.User;
import com.job.portal.model.enums.Role;
import com.job.portal.service.interfaces.ApplicationService;
import com.job.portal.service.interfaces.StudentProfileService;
import com.job.portal.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web Layer Tests for ApplicationApiController.
 *
 * @WebMvcTest(ApplicationApiController.class) spins up ONLY the web layer
 * (controllers, filters, MockMvc) without starting a real server or database.
 * All service dependencies must be mocked with @MockitoBean.
 *
 * MockMvc lets us fire fake HTTP requests at the controller and inspect
 * the response status code and JSON body — just like a real browser would.
 */
import org.springframework.context.annotation.Import;
import com.job.portal.config.SecurityConfig;

@WebMvcTest(ApplicationApiController.class)
@Import(SecurityConfig.class)
class ApplicationApiControllerTest {

    @MockitoBean
    private UserService userService; // Satisfies seedAdmin bean in application class

    // MockMvc is our "fake browser" — it sends HTTP requests to our controller.
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean replaces the real service with a Mockito dummy in the Spring context.
    @MockitoBean
    private ApplicationService applicationService;

    @MockitoBean
    private StudentProfileService profileService;

    // ObjectMapper lets us convert Java objects to JSON strings and back.
    @Autowired
    private ObjectMapper objectMapper;

    // A fake logged-in student user — we'll put this in the session for auth tests.
    private User loggedInStudent;

    @BeforeEach
    void setUp() {
        // Create a student user that will simulate being logged in.
        loggedInStudent = new User();
        loggedInStudent.setUserId(3L);
        loggedInStudent.setEmail("student@northeastern.edu");
        loggedInStudent.setRole(Role.STUDENT);
        loggedInStudent.setActive(true);
    }

    /**
     * TEST 3 — Unauthenticated Apply Returns 401 (Security Guard Test)
     *
     * SCENARIO: Someone calls POST /api/applications/apply WITHOUT being logged in
     *           (i.e., there is no "user" object in the session).
     * EXPECTED: The controller returns HTTP 401 with {"error": "Unauthorized"}.
     */
    @Test
    @DisplayName("Test 3: No session → POST /api/applications/apply returns 401 Unauthorized")
    void whenNoSession_thenApplyReturns401() throws Exception {
        // ACT & ASSERT: Fire the request with an EMPTY session (no user attribute set).
        mockMvc.perform(post("/api/applications/apply")
                        .param("jobId", "100")
                        .session(new MockHttpSession())) // empty session = not logged in
                .andExpect(status().isUnauthorized())   // HTTP 401
                .andExpect(jsonPath("$.error").value("Unauthorized")); // JSON body check
    }

    /**
     * TEST 4 — Logged-In Student Can Successfully Apply for a Job (Happy Path)
     *
     * SCENARIO: A logged-in student sends POST /api/applications/apply?jobId=100.
     * EXPECTED: The controller returns HTTP 200 with a success message and the application ID.
     */
    @Test
    @DisplayName("Test 4: Logged-in student → POST /api/applications/apply returns 200 with success message")
    void whenStudentApplies_thenReturns200WithSuccessMessage() throws Exception {
        // ARRANGE: Build a fake Application object that the service will "return".
        Application fakeApplication = new Application();
        fakeApplication.setApplicationId(42L); // The ID the DB would assign

        // Tell the mock service: "when apply() is called with any userId and jobId, return fakeApplication"
        when(applicationService.apply(anyLong(), anyLong()))
                .thenReturn(fakeApplication);

        // Build a session that has our student user already logged in.
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", loggedInStudent);

        // ACT & ASSERT: Fire the request WITH the logged-in session.
        mockMvc.perform(post("/api/applications/apply")
                        .param("jobId", "100")
                        .session(session))
                .andExpect(status().isOk())            // HTTP 200
                .andExpect(jsonPath("$.message").value("Application submitted successfully!"))
                .andExpect(jsonPath("$.id").value(42)); // The returned application ID
    }
}
