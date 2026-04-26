package com.job.portal.controller;

import com.job.portal.model.Application;
import com.job.portal.model.User;
import com.job.portal.model.enums.ApplicationStatus;
import com.job.portal.service.interfaces.ApplicationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationApiController {

    private final ApplicationService applicationService;
    private final com.job.portal.service.interfaces.StudentProfileService profileService;

    public ApplicationApiController(ApplicationService applicationService,
            com.job.portal.service.interfaces.StudentProfileService profileService) {
        this.applicationService = applicationService;
        this.profileService = profileService;
    }

    // This endpoint handles when a student clicks 'Apply' on a job
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestParam Long jobId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            // Save the application in the database
            Application app = applicationService.apply(user.getUserId(), jobId);
            return ResponseEntity
                    .ok(Map.of("message", "Application submitted successfully!", "id", app.getApplicationId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Allows a student to withdraw their application before it's processed
    @PostMapping("/withdraw/{id}")
    public ResponseEntity<?> withdraw(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            applicationService.withdraw(id);
            return ResponseEntity.ok(Map.of("message", "Application withdrawn successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Used by Employers to change the status of an application
    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestParam Long applicationId,
            @RequestParam String status,
            @RequestParam(required = false) String notes,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            ApplicationStatus newStatus = ApplicationStatus.valueOf(status.toUpperCase());
            applicationService.updateStatus(applicationId, newStatus, notes);
            return ResponseEntity.ok(Map.of("message", "Status updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // A secure endpoint for Employers to view a student's resume
    @GetMapping("/resume/{userId}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadResume(@PathVariable Long userId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");

        // SECURITY CHECK: Only Employers and Admins can view resumes
        if (user == null || (user.getRole() != com.job.portal.model.enums.Role.EMPLOYER
                && user.getRole() != com.job.portal.model.enums.Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        try {
            // Fetch the profile to get the stored filename
            com.job.portal.model.StudentProfile profile = profileService.getProfile(userId);
            if (profile.getResumeFileName() == null) {
                return ResponseEntity.notFound().build();
            }

            // Find the file on the server's disk
            java.nio.file.Path filePath = com.job.portal.util.FileStorageUtil
                    .getResumePath(profile.getResumeFileName());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(
                    filePath.toUri());

            if (resource.exists()) {
                // Stream the file directly to the browser
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType(profile.getResumeContentType()))
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + profile.getResumeFileName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
