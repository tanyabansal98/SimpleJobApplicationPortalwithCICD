package com.job.portal.service.impl;

import com.job.portal.model.Application;
import com.job.portal.model.Job;
import com.job.portal.model.User;
import com.job.portal.model.enums.ApplicationStatus;
import com.job.portal.dao.ApplicationDAO;
import com.job.portal.dao.JobDAO;
import com.job.portal.dao.UserDAO;
import com.job.portal.service.interfaces.ApplicationService;
import com.job.portal.service.interfaces.StudentProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

// Implements the full application lifecycle — applying, withdrawing, and status updates.
@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationDAO applicationDAO;
    private final UserDAO userDAO;
    private final JobDAO jobDAO;
    private final StudentProfileService profileService;

    public ApplicationServiceImpl(ApplicationDAO applicationDAO,
            UserDAO userDAO,
            JobDAO jobDAO,
            StudentProfileService profileService) {
        this.applicationDAO = applicationDAO;
        this.userDAO = userDAO;
        this.jobDAO = jobDAO;
        this.profileService = profileService;
    }

    // Validates the student and job exist, guards against duplicate applications,
    // then saves the new application.
    @Override
    public Application apply(Long studentUserId, Long jobId) {

        User student = userDAO.findById(studentUserId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentUserId));

        Job job = jobDAO.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        if (!job.getActive()) {
            throw new IllegalArgumentException("Job is no longer active.");
        }

        Optional<Application> existingOpt = applicationDAO.findByStudent_UserIdAndJob_JobId(studentUserId, jobId);
        if (existingOpt.isPresent()) {
            Application existing = existingOpt.get();
            if (existing.getStatus() == ApplicationStatus.WITHDRAWN) {
                // Reactivate withdrawn application
                existing.setStatus(ApplicationStatus.SUBMITTED);
                return applicationDAO.save(existing);
            } else {
                throw new IllegalArgumentException("You have already applied to this job.");
            }
        }

        // RESUME REQUIRED: Block the application if the student has not uploaded a resume.
        // This check runs server-side so it cannot be bypassed from the UI.
        try {
            com.job.portal.model.StudentProfile profile = profileService.getProfile(studentUserId);
            if (profile == null || profile.getResumeFileName() == null || profile.getResumeFileName().isBlank()) {
                throw new IllegalArgumentException(
                        "You must upload a resume before applying. Please update your profile first.");
            }
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw the resume-required error as-is
        } catch (Exception e) {
            // Profile doesn't exist at all — treat as no resume
            throw new IllegalArgumentException(
                    "You must upload a resume before applying. Please update your profile first.");
        }

        // Snapshot the student's current resume at the moment of applying.
        // This freezes their resume for THIS application permanently, so the employer
        // always sees the exact resume the student submitted — even if the student
        // uploads a new resume later for a different job.
        String resumeFile = null;
        String resumeContentType = null;
        try {
            com.job.portal.model.StudentProfile profile = profileService.getProfile(studentUserId);
            if (profile != null) {
                resumeFile = profile.getResumeFileName();
                resumeContentType = profile.getResumeContentType();
            }
        } catch (Exception ignored) {
            // Should not reach here since we already checked above
        }

        Application app = Application.builder()
                .student(student)
                .job(job)
                .status(ApplicationStatus.SUBMITTED)
                .resumeFileAtApply(resumeFile)
                .resumeContentTypeAtApply(resumeContentType)
                .build();

        return applicationDAO.save(app);
    }

    // Only SUBMITTED applications can be withdrawn — anything already in progress
    // is locked.
    @Override
    public Application withdraw(Long applicationId) {
        Application app = applicationDAO.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        if (app.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new IllegalArgumentException(
                    "Only submitted applications can be withdrawn. This application is already under process.");
        }

        app.setStatus(ApplicationStatus.WITHDRAWN);
        return applicationDAO.save(app);
    }

    // Lets an employer advance or reject an application.
    @Override
    public Application updateStatus(Long applicationId, ApplicationStatus newStatus, String employerNotes) {
        // Find the application and update its status (e.g. from 'Applied' to
        // 'Interviewing')
        Application app = applicationDAO.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        app.setStatus(newStatus);
        if (employerNotes != null)
            app.setEmployerNotes(employerNotes);

        return applicationDAO.save(app);
    }

    // Returns the student's full application history so they can track every role
    // they've pursued.
    @Override
    public List<Application> getByStudent(Long studentUserId) {
        return applicationDAO.findByStudent_UserId(studentUserId);
    }

    // Retrieves everyone who has applied for a given job — used on the employer's
    // applications page.
    @Override
    public List<Application> getByJob(Long jobId) {
        return applicationDAO.findByJob_JobId(jobId);
    }

    // Aggregates applications from all of an employer's listings in one go.
    @Override
    public List<Application> getByEmployer(Long employerUserId) {
        return applicationDAO.findByJob_Employer_UserId(employerUserId);
    }

    // Fetches every application in the database — intended for admin reporting.
    @Override
    public List<Application> getAll() {
        return applicationDAO.findAll();
    }

    // Returns a single application or null if it can't be found.
    @Override
    public Application getById(Long applicationId) {
        return applicationDAO.findById(applicationId).orElse(null);
    }
}
