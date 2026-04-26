package com.job.portal.controller;

import com.job.portal.model.User;
import com.job.portal.service.interfaces.ApplicationService;
import com.job.portal.service.interfaces.JobService;
import com.job.portal.service.interfaces.StudentProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Handles all the actions a student can do once they're signed in.

@Controller
@RequestMapping("/student")
public class StudentController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final StudentProfileService profileService;

    public StudentController(JobService jobService,
            ApplicationService applicationService,
            StudentProfileService profileService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.profileService = profileService;
    }

    // The student's personal overview page - Shows all of their active job
    // applications

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        // Only show applications the student is still actively pursuing.
        java.util.List<com.job.portal.model.Application> activeApps = applicationService.getByStudent(user.getUserId())
                .stream()
                .filter(app -> app.getStatus() != com.job.portal.model.enums.ApplicationStatus.WITHDRAWN)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("applications", activeApps);
        return "student/dashboard";
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
        User user = (User) session.getAttribute("user");
        model.addAttribute("jobs", jobService.listJobs(title, location));

        // Track jobs student has already applied to (exclude withdrawn).
        java.util.Set<Long> appliedJobIds = applicationService.getByStudent(user.getUserId()).stream()
                .filter(app -> app.getStatus() != com.job.portal.model.enums.ApplicationStatus.WITHDRAWN)
                .map(app -> app.getJob().getJobId())
                .collect(java.util.stream.Collectors.toSet());
        model.addAttribute("appliedJobIds", appliedJobIds);

        return "student/jobs";
    }

    // Opens the detail page for a single job listing.

    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("job", jobService.getJob(id));

        // Check if the student already has a live (non-withdrawn) application for this
        // job.
        boolean alreadyApplied = applicationService.getByStudent(user.getUserId()).stream()
                .filter(app -> app.getStatus() != com.job.portal.model.enums.ApplicationStatus.WITHDRAWN)
                .anyMatch(app -> app.getJob().getJobId().equals(id));
        model.addAttribute("alreadyApplied", alreadyApplied);

        return "student/job_details";
    }

    // Loads the student's profile page

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("profile", profileService.getProfile(user.getUserId()));
        return "student/profile";
    }

    // Handles resume file uploads from the profile page.

    @org.springframework.web.bind.annotation.PostMapping("/api/resume")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> uploadResume(
            @org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            profileService.uploadResume(user.getUserId(), file);
            com.job.portal.model.StudentProfile profile = profileService.getProfile(user.getUserId());
            response.put("success", true);
            // Send back the stored filename so the UI can reflect the change right away.
            response.put("fileName", profile.getResumeFileName());
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            // Something went wrong — let the frontend know so it can show an error.
            response.put("success", false);
            response.put("message", e.getMessage());
            return org.springframework.http.ResponseEntity.badRequest().body(response);
        }
    }
}
