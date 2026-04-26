package com.job.portal.dao;

import com.job.portal.model.Application;
import com.job.portal.model.enums.ApplicationStatus;
import java.util.List;
import java.util.Optional;

public interface ApplicationDAO {

    // Fetches a single application by its unique ID.
    Optional<Application> findById(Long applicationId);

    // Saves a new application or updates an existing one.
    Application save(Application application);

    // Returns every application ever submitted across the portal.
    List<Application> findAll();

    // Returns the total number of applications in the system.
    long count();

    // Returns all applications submitted by a specific student.
    List<Application> findByStudent_UserId(Long studentUserId);

    // Checks whether a student has already applied to a specific job.
    Optional<Application> findByStudent_UserIdAndJob_JobId(Long studentUserId, Long jobId);

    // Returns all applications received for a specific job posting.
    List<Application> findByJob_JobId(Long jobId);

    // Filters applications by their current status (e.g., PENDING, WITHDRAWN).
    List<Application> findByStatus(ApplicationStatus status);

    // Returns all applications submitted for any job posted by a given employer.
    List<Application> findByJob_Employer_UserId(Long employerUserId);
}
