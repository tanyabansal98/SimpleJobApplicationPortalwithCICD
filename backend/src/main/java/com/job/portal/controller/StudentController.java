package com.job.portal.controller;

import com.job.portal.dto.JobMatchDTO;
import com.job.portal.model.User;
import com.job.portal.model.StudentProfile;
import com.job.portal.model.EmployerProfile;
import com.job.portal.model.Job;
import com.job.portal.service.interfaces.ApplicationService;
import com.job.portal.service.interfaces.JobService;
import com.job.portal.service.interfaces.StudentProfileService;
import com.job.portal.service.interfaces.EmployerProfileService;
import com.job.portal.service.impl.QdrantSearchService;
import com.job.portal.util.EmbeddingUtil;
import com.job.portal.service.interfaces.JobRerankingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Handles all the actions a student can do once they're signed in.

@Controller
@RequestMapping("/student")
public class StudentController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final StudentProfileService profileService;
    private final QdrantSearchService qdrantSearchService;
    private final EmployerProfileService employerProfileService;
    private final JobRerankingService jobRerankingService;

    public StudentController(JobService jobService,
            ApplicationService applicationService,
            StudentProfileService profileService,
            QdrantSearchService qdrantSearchService,
            EmployerProfileService employerProfileService,
            JobRerankingService jobRerankingService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.profileService = profileService;
        this.qdrantSearchService = qdrantSearchService;
        this.employerProfileService = employerProfileService;
        this.jobRerankingService = jobRerankingService;
    }

    // The student's personal overview page - Shows all of their active job
    // applications

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            // Only show applications the student is still actively pursuing.
            List<com.job.portal.model.Application> activeApps = applicationService.getByStudent(user.getUserId())
                    .stream()
                    .filter(app -> app.getStatus() != com.job.portal.model.enums.ApplicationStatus.WITHDRAWN)
                    .collect(Collectors.toList());
            model.addAttribute("applications", activeApps);
            return "student/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "student/dashboard";
        }
    }

    /**
     * The main job board — where students can view and apply for jobs.
     * Supports optional filtering by job title and location
     */

    @GetMapping("/jobs")
    public String browseJobs(@RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            HttpSession session,
            Model model) {
        try {
            User user = (User) session.getAttribute("user");
            model.addAttribute("jobs", jobService.listJobs(title, location));

            // Track jobs student has already applied to (exclude withdrawn).
            Set<Long> appliedJobIds = applicationService.getByStudent(user.getUserId()).stream()
                    .filter(app -> app.getStatus() != com.job.portal.model.enums.ApplicationStatus.WITHDRAWN)
                    .map(app -> app.getJob().getJobId())
                    .collect(Collectors.toSet());
            model.addAttribute("appliedJobIds", appliedJobIds);

            // Check if student has a resume — Apply buttons are disabled without one.
            boolean hasResume = false;
            try {
                StudentProfile profile = profileService.getProfile(user.getUserId());
                hasResume = profile != null && profile.getResumeFileName() != null && !profile.getResumeFileName().isBlank();
            } catch (Exception ignored) {}
            model.addAttribute("hasResume", hasResume);

            return "student/jobs";
        } catch (Exception e) {
            model.addAttribute("error", "Error browsing jobs: " + e.getMessage());
            return "student/dashboard";
        }
    }

    /**
     * Renders the recommended jobs view for the logged-in student.
     */
    @GetMapping("/jobs/matches")
    public String getRecommendedJobs(HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            
            // Check if student has a resume — Apply buttons are disabled without one.
            boolean hasResume = false;
            try {
                StudentProfile profile = profileService.getProfile(user.getUserId());
                hasResume = profile != null && profile.getResumeFileName() != null && !profile.getResumeFileName().isBlank();
            } catch (Exception ignored) {}
            model.addAttribute("hasResume", hasResume);

            return "student/recommended_jobs";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading matches: " + e.getMessage());
            return "student/dashboard";
        }
    }

    // Opens the detail page for a single job listing.

    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable Long id, HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            model.addAttribute("job", jobService.getJob(id));

            // Check if the student already has a live (non-withdrawn) application for this job.
            boolean alreadyApplied = applicationService.getByStudent(user.getUserId()).stream()
                    .filter(app -> app.getStatus() != com.job.portal.model.enums.ApplicationStatus.WITHDRAWN)
                    .anyMatch(app -> app.getJob().getJobId().equals(id));
            model.addAttribute("alreadyApplied", alreadyApplied);

            // Check if student has a resume — Apply button is disabled without one.
            boolean hasResume = false;
            try {
                StudentProfile profile = profileService.getProfile(user.getUserId());
                hasResume = profile != null && profile.getResumeFileName() != null && !profile.getResumeFileName().isBlank();
            } catch (Exception ignored) {}
            model.addAttribute("hasResume", hasResume);

            return "student/job_details";
        } catch (Exception e) {
            model.addAttribute("error", "Error viewing job details: " + e.getMessage());
            return "student/dashboard";
        }
    }

    // Loads the student's profile page

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            model.addAttribute("profile", profileService.getProfile(user.getUserId()));
            return "student/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "student/dashboard";
        }
    }

    // Handles resume file uploads from the profile page.

    @PostMapping("/api/resume")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadResume(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        try {
            profileService.uploadResume(user.getUserId(), file);
            StudentProfile profile = profileService.getProfile(user.getUserId());
            response.put("success", true);
            // Send back the stored filename so the UI can reflect the change right away.
            response.put("fileName", profile.getResumeFileName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Something went wrong — let the frontend know so it can show an error.
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Finds jobs whose descriptions are most similar to this student's resume,
    // using the vector embeddings stored in Qdrant.

    @GetMapping("/api/jobs/matches")
    @ResponseBody
    public ResponseEntity<?> getMatchingJobs(
            @RequestParam(defaultValue = "10") int topN,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            StudentProfile profile = profileService.getProfile(user.getUserId());
            if (profile.getResumeEmbedding() == null) {
                return ResponseEntity.badRequest().body("No resume uploaded yet");
            }

            float[] queryVector = EmbeddingUtil.fromJson(profile.getResumeEmbedding());
            Map<Long, Double> jobIdToScore = qdrantSearchService.searchSimilarJobIds(queryVector, topN);

            // Compute these ONCE, reused for every match — same pattern as browseJobs().
            boolean hasResume = profile.getResumeFileName() != null && !profile.getResumeFileName().isBlank();
            Set<Long> appliedJobIds = applicationService.getByStudent(user.getUserId()).stream()
                    .filter(app -> app.getStatus() != com.job.portal.model.enums.ApplicationStatus.WITHDRAWN)
                    .map(app -> app.getJob().getJobId())
                    .collect(Collectors.toSet());

            List<JobMatchDTO> matches = new ArrayList<>();
            for (Map.Entry<Long, Double> entry : jobIdToScore.entrySet()) {
                Long jobId = entry.getKey();
                try {
                    Job job = jobService.getJob(jobId); // real, current data from Postgres

                    // Company name lives on EmployerProfile, not User, so we look it up separately.
                    String companyName = "";
                    if (job.getEmployer() != null) {
                        try {
                            EmployerProfile employerProfile = employerProfileService.getProfile(job.getEmployer().getUserId());
                            companyName = employerProfile.getCompanyName() != null ? employerProfile.getCompanyName() : "";
                        } catch (Exception ignored) {
                            // If the employer profile can't be found for some reason, just leave companyName blank
                            // rather than failing the whole matches request.
                        }
                    }

                    JobMatchDTO dto = new JobMatchDTO();
                    dto.setJobId(job.getJobId());
                    dto.setTitle(job.getTitle());
                    dto.setDescription(job.getDescription());
                    dto.setRequiredSkills(job.getRequiredSkills());
                    dto.setLocation(job.getLocation());
                    dto.setCompanyName(companyName);
                    dto.setScore(entry.getValue());
                    dto.setAlreadyApplied(appliedJobIds.contains(jobId));
                    dto.setHasResume(hasResume);

                    matches.add(dto);
                } catch (Exception e) {
                    // Job may have been deleted since it was embedded — skip it rather than fail the whole request.
                }
            }
            matches = jobRerankingService.rerank(profile.getResumeText(), matches);
            return ResponseEntity.ok(matches);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to fetch matches: " + e.getMessage());
        }
    }
}