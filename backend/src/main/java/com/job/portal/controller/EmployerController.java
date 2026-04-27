package com.job.portal.controller;

import com.job.portal.model.Job;
import com.job.portal.model.User;
import com.job.portal.service.interfaces.ApplicationService;
import com.job.portal.service.interfaces.EmployerProfileService;
import com.job.portal.service.interfaces.JobService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final EmployerProfileService profileService;

    public EmployerController(JobService jobService,
            ApplicationService applicationService,
            EmployerProfileService profileService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.profileService = profileService;
    }

    // Shows a snapshot of all the jobs this employer has posted and manage
    // applications.

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            model.addAttribute("jobs", jobService.listJobsByEmployer(user.getUserId()));
            return "employer/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "employer/dashboard";
        }
    }

    // Opens the "post a new job" form.
    @GetMapping("/jobs/new")
    public String showPostJobForm(Model model) {
        try {
            return "employer/post_job";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading job form: " + e.getMessage());
            return "employer/dashboard";
        }
    }

    // Saves a newly submitted job listing.

    @PostMapping("/jobs/new")
    public String postJob(@ModelAttribute Job job, HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            // Link the job to this employer before saving.
            job.setEmployer(user);
            jobService.createJob(job);
            return "redirect:/employer/dashboard?success=Job posted successfully.";
        } catch (Exception e) {
            model.addAttribute("error", "Error posting job: " + e.getMessage());
            return "employer/post_job";
        }
    }

    // Shows all applications for a specific job listing.

    @GetMapping("/applications/{jobId}")
    public String viewApplications(@PathVariable Long jobId, Model model) {
        try {
            model.addAttribute("job", jobService.getJob(jobId));
            // Only show active applications; skip anything the student has pulled back.
            java.util.List<com.job.portal.model.Application> activeApps = applicationService.getByJob(jobId).stream()
                    .filter(app -> app.getStatus() != com.job.portal.model.enums.ApplicationStatus.WITHDRAWN)
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("applications", activeApps);
            return "employer/applications";
        } catch (Exception e) {
            model.addAttribute("error", "Error viewing applications: " + e.getMessage());
            return "employer/dashboard";
        }
    }

    // Loads the employer's company profile page.

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            model.addAttribute("profile", profileService.getProfile(user.getUserId()));
            return "employer/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "employer/dashboard";
        }
    }
}
