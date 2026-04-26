package com.job.portal.dao.impl;

import com.job.portal.dao.JobDAO;
import com.job.portal.model.Job;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class JobDAOImpl implements JobDAO {

    private final SessionFactory sessionFactory;

    public JobDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // Fetches a specific job by ID.
    @Override
    public Optional<Job> findById(Long jobId) {
        String hql = "FROM Job j JOIN FETCH j.employer e LEFT JOIN FETCH e.employerProfile WHERE j.jobId = :jobId";
        return getSession().createQuery(hql, Job.class)
                .setParameter("jobId", jobId)
                .uniqueResultOptional();
    }

    // Saves a new job posting or updates an existing one
    @Override
    public Job save(Job job) {
        if (job.getJobId() == null) {
            getSession().persist(job);
        } else {
            job = getSession().merge(job);
        }
        return job;
    }

    // Returns all jobs in the system along with who posted them
    @Override
    public List<Job> findAll() {
        return getSession().createQuery("FROM Job j JOIN FETCH j.employer", Job.class).list();
    }

    // Counts total job postings
    @Override
    public long count() {
        return getSession().createQuery("SELECT count(j) FROM Job j", Long.class).uniqueResult();
    }

    // Search functionality: finds jobs by title (case-insensitive)
    @Override
    public List<Job> findByTitleContainingIgnoreCase(String title) {
        String hql = "FROM Job j JOIN FETCH j.employer WHERE lower(j.title) LIKE :title";
        Query<Job> query = getSession().createQuery(hql, Job.class);
        query.setParameter("title", "%" + title.toLowerCase() + "%");
        return query.list();
    }

    // Search functionality: finds jobs by location
    @Override
    public List<Job> findByLocationContainingIgnoreCase(String location) {
        String hql = "FROM Job j JOIN FETCH j.employer WHERE lower(j.location) LIKE :location";
        Query<Job> query = getSession().createQuery(hql, Job.class);
        query.setParameter("location", "%" + location.toLowerCase() + "%");
        return query.list();
    }

    // Lists all jobs posted by a specific employer
    @Override
    public List<Job> findByEmployer_UserId(Long employerUserId) {
        String hql = "FROM Job j JOIN FETCH j.employer WHERE j.employer.userId = :userId";
        Query<Job> query = getSession().createQuery(hql, Job.class);
        query.setParameter("userId", employerUserId);
        return query.list();
    }

    // Only shows jobs that are currently 'Active' (not closed or expired)
    @Override
    public List<Job> findByActiveTrue() {
        String hql = "FROM Job j JOIN FETCH j.employer WHERE j.active = true";
        return getSession().createQuery(hql, Job.class).list();
    }
}
