package com.job.portal.dao;

import com.job.portal.model.Job;
import java.util.List;
import java.util.Optional;

public interface JobDAO {

    // Fetches a single job posting by its unique ID.
    Optional<Job> findById(Long jobId);

    // Saves a new job posting or updates an existing one.
    Job save(Job job);

    // Returns every job in the system, regardless of status.
    List<Job> findAll();

    // Returns the total number of job postings.
    long count();

    // Searches for jobs whose title contains the given keyword (case-insensitive).
    List<Job> findByTitleContainingIgnoreCase(String title);

    // Searches for jobs in a specific location (case-insensitive).
    List<Job> findByLocationContainingIgnoreCase(String location);

    // Returns all jobs posted by a specific employer.
    List<Job> findByEmployer_UserId(Long employerUserId);

    // Returns only jobs that are currently marked as active/open.
    List<Job> findByActiveTrue();
}
