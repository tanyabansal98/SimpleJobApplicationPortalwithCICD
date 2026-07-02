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

    public ApplicationApiController(ApplicationService applicationService) {
        this.applicationService = applicationService;
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

    // A secure endpoint for Employers to view the resume a student submitted with a specific application.
    // Serves the SNAPSHOTTED resume (frozen at apply-time), not the student's current resume.
    @GetMapping("/resume/{applicationId}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadResume(@PathVariable Long applicationId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");

        // SECURITY CHECK: Only Employers and Admins can view resumes
        if (user == null || (user.getRole() != com.job.portal.model.enums.Role.EMPLOYER
                && user.getRole() != com.job.portal.model.enums.Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        try {
            // Fetch the application — the resume file is stored directly on this record
            com.job.portal.model.Application application = applicationService.getById(applicationId);
            if (application == null || application.getResumeFileAtApply() == null) {
                return ResponseEntity.notFound().build();
            }

            // Resolve the snapshotted resume file on disk
            java.nio.file.Path filePath = com.job.portal.util.FileStorageUtil
                    .getResumePath(application.getResumeFileAtApply());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(
                    filePath.toUri());

            if (resource.exists()) {
                String contentType = application.getResumeContentTypeAtApply() != null
                        ? application.getResumeContentTypeAtApply()
                        : "application/octet-stream";
                // Stream the snapshotted resume file to the browser
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + application.getResumeFileAtApply() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
