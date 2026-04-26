package com.job.portal.service.interfaces;

import com.job.portal.model.Application;
import com.job.portal.model.enums.ApplicationStatus;

import java.util.List;

// Handles everything related to a student applying for a job and employers responding.
public interface ApplicationService {

    // Submits a new job application on behalf of a student.
    Application apply(Long studentUserId, Long jobId);

    // Lets a student pull back an application they've changed their mind about.
    Application withdraw(Long applicationId);

    // Lets an employer move an application through stages (e.g., to Interviewing or Rejected).
    Application updateStatus(Long applicationId, ApplicationStatus newStatus, String employerNotes);

    // Fetches all applications submitted by a particular student.
    List<Application> getByStudent(Long studentUserId);

    // Fetches all applications received for a specific job posting.
    List<Application> getByJob(Long jobId);

    // Fetches all applications across every job an employer has posted.
    List<Application> getByEmployer(Long employerUserId);

    // Returns every application in the system (admin use).
    List<Application> getAll();

    // Retrieves a single application by its ID.
    Application getById(Long applicationId);
}
