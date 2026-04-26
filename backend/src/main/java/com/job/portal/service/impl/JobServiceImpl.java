package com.job.portal.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.portal.model.Job;
import com.job.portal.model.User;
import com.job.portal.dao.JobDAO;
import com.job.portal.dao.UserDAO;
import com.job.portal.service.interfaces.JobService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

// Implements job listing logic, including Redis caching of the active-jobs list for fast page loads.
@Service
@Transactional
public class JobServiceImpl implements JobService {

    private static final String JOBS_CACHE_KEY = "active_jobs_list";

    private final JobDAO jobDAO;
    private final UserDAO userDAO;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public JobServiceImpl(JobDAO jobDAO, 
                          UserDAO userDAO,
                          RedisTemplate<String, String> redisTemplate,
                          ObjectMapper objectMapper) {
        this.jobDAO = jobDAO;
        this.userDAO = userDAO;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // Clears the Redis cache whenever jobs change so students always see fresh listings.
    private void evictCache() {
        redisTemplate.delete(JOBS_CACHE_KEY);
    }

    // Saves the new job, ties it to the verified employer, then busts the cache so it appears immediately.
    @Override
    public Job createJob(Job job) {
        
        User employer = userDAO.findById(job.getEmployer().getUserId())
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        job.setEmployer(employer);

        Job saved = jobDAO.save(job);
        evictCache();
        return saved;
    }

    // Updates editable fields on an existing posting and refreshes the cache.
    @Override
    public Job updateJob(Long jobId, Job job) {
        Job existing = getJob(jobId);
        existing.setTitle(job.getTitle());
        existing.setDescription(job.getDescription());
        existing.setLocation(job.getLocation());
        
        Job saved = jobDAO.save(existing);
        evictCache();
        return saved;
    }

    // Soft-deletes by flipping the active flag to false — the record is kept but hidden from students.
    @Override
    public void deleteJob(Long jobId) {
        Job job = getJob(jobId);
        job.setActive(false);
        jobDAO.save(job);
        evictCache();
    }

    // Tries Redis first; if the cache is cold it hits the DB and caches the result for 10 minutes.
    @Override
    public List<Job> listActiveJobs() {
        try {
            String cachedJson = redisTemplate.opsForValue().get(JOBS_CACHE_KEY);
            if (cachedJson != null) {
                return objectMapper.readValue(cachedJson, new TypeReference<List<Job>>() {});
            }
        } catch (Exception e) {
        }

        List<Job> jobs = jobDAO.findByActiveTrue();

        try {
            String json = objectMapper.writeValueAsString(jobs);
            redisTemplate.opsForValue().set(JOBS_CACHE_KEY, json, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
        }

        return jobs;
    }

    // Routes to a title or location search if filters are provided, otherwise returns the cached active list.
    @Override
    public List<Job> listJobs(String title, String location) {
        if (title != null && !title.isBlank())
            return jobDAO.findByTitleContainingIgnoreCase(title);
        if (location != null && !location.isBlank())
            return jobDAO.findByLocationContainingIgnoreCase(location);
        return listActiveJobs(); // Use cached list if no filters
    }

    // Returns all jobs posted by the given employer, for the employer dashboard.
    @Override
    public List<Job> listJobsByEmployer(Long employerUserId) {
        return jobDAO.findByEmployer_UserId(employerUserId);
    }

    // Fetches a single job or throws a clear error if the ID doesn't match anything.
    @Override
    public Job getJob(Long jobId) {
        return jobDAO.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
    }
}
