package com.job.portal.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.portal.model.Job;
import com.job.portal.model.User;
import com.job.portal.dao.JobDAO;
import com.job.portal.dao.UserDAO;
import com.job.portal.service.interfaces.EmbeddingService;
import com.job.portal.service.interfaces.JobService;
import com.job.portal.service.interfaces.QdrantService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    private static final String JOBS_CACHE_KEY = "active_jobs_list";
    private static final String QDRANT_JOBS_COLLECTION = "jobs";

    private final JobDAO jobDAO;
    private final UserDAO userDAO;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final EmbeddingService embeddingService;
    private final QdrantService qdrantService;

    public JobServiceImpl(JobDAO jobDAO,
                          UserDAO userDAO,
                          RedisTemplate<String, String> redisTemplate,
                          ObjectMapper objectMapper,
                          EmbeddingService embeddingService,
                          QdrantService qdrantService) {
        this.jobDAO = jobDAO;
        this.userDAO = userDAO;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.embeddingService = embeddingService;
        this.qdrantService = qdrantService;
    }

    private void evictCache() {
        redisTemplate.delete(JOBS_CACHE_KEY);
    }

    // Builds one combined text block from a job's key fields, so the embedding
    // captures the job's full meaning rather than just one isolated field.
    private String buildJobEmbeddingText(Job job) {
        StringBuilder sb = new StringBuilder();
        sb.append(job.getTitle() != null ? job.getTitle() : "");
        sb.append(". ");
        sb.append(job.getDescription() != null ? job.getDescription() : "");
        sb.append(". ");
        sb.append(job.getRequiredSkills() != null ? job.getRequiredSkills() : "");
        return sb.toString();
    }

    // Generates the job's embedding and stores it in Qdrant, tagged with its jobId.
    private void embedAndStoreJob(Job job) {
        String text = buildJobEmbeddingText(job);
        float[] vector = embeddingService.generateEmbedding(text);

        Map<String, Object> payload = Map.of(
                "jobId", job.getJobId(),
                "title", job.getTitle() != null ? job.getTitle() : ""
        );

        qdrantService.upsertVector(QDRANT_JOBS_COLLECTION, job.getJobId(), vector, payload);
    }

    // TEMPORARY: one-time backfill for jobs that existed before Qdrant embedding was added.
    // Loops through every job in Postgres and pushes its embedding into Qdrant.
    // Safe to run multiple times — embedAndStoreJob() just overwrites the same jobId each time.
    public int backfillJobEmbeddings() {
    List<Job> allJobs = jobDAO.findAll();
    int successCount = 0;
        for (Job job : allJobs) {
            try {
                embedAndStoreJob(job);
                successCount++;
            } catch (Exception e) {
                System.err.println("Failed to embed job " + job.getJobId() + ": " + e.getMessage());
            }
        }
        return successCount;
    }

    @Override
    public Job createJob(Job job) {

        User employer = userDAO.findById(job.getEmployer().getUserId())
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        job.setEmployer(employer);

        Job saved = jobDAO.save(job);
        evictCache();

        // NEW: embed the job and store its vector in Qdrant for future matching.
        embedAndStoreJob(saved);

        return saved;
    }

    @Override
    public Job updateJob(Long jobId, Job job) {
        Job existing = getJob(jobId);
        existing.setTitle(job.getTitle());
        existing.setDescription(job.getDescription());
        existing.setLocation(job.getLocation());

        Job saved = jobDAO.save(existing);
        evictCache();

        // NEW: re-embed since title/description may have changed — keeps the vector accurate.
        embedAndStoreJob(saved);

        return saved;
    }

    @Override
    public void deleteJob(Long jobId) {
        Job job = getJob(jobId);
        job.setActive(false);
        jobDAO.save(job);
        evictCache();

        // NEW: remove the vector too, since inactive jobs should never appear in matches.
        qdrantService.deleteVector(QDRANT_JOBS_COLLECTION, jobId);
    }

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

    @Override
    public List<Job> listJobs(String title, String location) {
        if (title != null && !title.isBlank())
            return jobDAO.findByTitleContainingIgnoreCase(title);
        if (location != null && !location.isBlank())
            return jobDAO.findByLocationContainingIgnoreCase(location);
        return listActiveJobs();
    }

    @Override
    public List<Job> listJobsByEmployer(Long employerUserId) {
        return jobDAO.findByEmployer_UserId(employerUserId);
    }

    @Override
    public Job getJob(Long jobId) {
        return jobDAO.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
    }
}