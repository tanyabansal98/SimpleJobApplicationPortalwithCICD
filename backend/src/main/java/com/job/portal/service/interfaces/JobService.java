package com.job.portal.service.interfaces;

import com.job.portal.model.Job;

import java.util.List;

// Business logic for creating, updating, deleting, and searching job listings.
public interface JobService {

    // Saves a brand-new job posting and links it to the employer who created it.
    Job createJob(Job job);

    // Updates an existing job's title, description, or location.
    Job updateJob(Long jobId, Job job);

    // Soft-deletes a job by marking it inactive so students can no longer see it.
    void deleteJob(Long jobId);

    // Returns jobs filtered by title or location; falls back to the full active list if no filters are given.
    List<Job> listJobs(String title, String location);

    // Returns all currently active job listings (uses Redis cache for speed).
    List<Job> listActiveJobs();

    // Returns all jobs posted by a specific employer.
    List<Job> listJobsByEmployer(Long employerUserId);

    // Fetches a single job by ID, throwing an error if it doesn't exist.
    Job getJob(Long jobId);
}
