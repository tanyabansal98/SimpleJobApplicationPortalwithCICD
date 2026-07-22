package com.job.portal.controller;

import com.job.portal.model.User;
import com.job.portal.model.enums.Role;
import com.job.portal.service.interfaces.ApplicationService;
import com.job.portal.service.interfaces.EmployerProfileService;
import com.job.portal.service.interfaces.JobRerankingService;
import com.job.portal.service.interfaces.JobService;
import com.job.portal.service.interfaces.StudentProfileService;
import com.job.portal.service.interfaces.UserService;
import com.job.portal.service.impl.QdrantSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Import;
import com.job.portal.config.SecurityConfig;

@WebMvcTest(StudentController.class)
@Import(SecurityConfig.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JobService jobService;

    @MockitoBean
    private ApplicationService applicationService;

    @MockitoBean
    private StudentProfileService profileService;

    @MockitoBean
    private QdrantSearchService qdrantSearchService;

    @MockitoBean
    private EmployerProfileService employerProfileService;

    @MockitoBean
    private JobRerankingService jobRerankingService;

    private User loggedInStudent;

    @BeforeEach
    void setUp() {
        loggedInStudent = new User();
        loggedInStudent.setUserId(3L);
        loggedInStudent.setEmail("student@northeastern.edu");
        loggedInStudent.setRole(Role.STUDENT);
        loggedInStudent.setActive(true);
    }

    @Test
    @DisplayName("Verify /student/jobs/matches routes correctly without path variable conflict")
    void testRecommendedJobsRoute() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", loggedInStudent);

        mockMvc.perform(get("/student/jobs/matches").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("student/recommended_jobs"))
                .andExpect(model().attributeExists("hasResume"));
    }

    @Test
    @DisplayName("Verify /student/jobs/{id} routes correctly for a Long ID")
    void testViewJobDetailRoute() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", loggedInStudent);

        // Stub getJob to return a fake job so it doesn't fail
        com.job.portal.model.Job job = new com.job.portal.model.Job();
        job.setJobId(123L);
        org.mockito.Mockito.when(jobService.getJob(123L)).thenReturn(job);

        mockMvc.perform(get("/student/jobs/123").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("student/job_details"));
    }
}
