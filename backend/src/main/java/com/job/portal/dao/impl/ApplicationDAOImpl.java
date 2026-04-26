package com.job.portal.dao.impl;

import com.job.portal.dao.ApplicationDAO;
import com.job.portal.model.Application;
import com.job.portal.model.enums.ApplicationStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class ApplicationDAOImpl implements ApplicationDAO {

    private final SessionFactory sessionFactory;

    public ApplicationDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // Fetches a single application by its ID.
    @Override
    public Optional<Application> findById(Long applicationId) {
        String hql = "FROM Application a JOIN FETCH a.student JOIN FETCH a.job j JOIN FETCH j.employer WHERE a.applicationId = :id";
        return getSession().createQuery(hql, Application.class)
                .setParameter("id", applicationId)
                .uniqueResultOptional();
    }

    // Saves or updates an application
    @Override
    public Application save(Application application) {
        if (application.getApplicationId() == null) {
            getSession().persist(application);
        } else {
            application = getSession().merge(application);
        }
        return application;
    }

    // Returns every single application in the system
    @Override
    public List<Application> findAll() {
        String hql = "FROM Application a JOIN FETCH a.student JOIN FETCH a.job j JOIN FETCH j.employer";
        return getSession().createQuery(hql, Application.class).list();
    }

    // Counts total applications submitted across the entire portal
    @Override
    public long count() {
        return getSession().createQuery("SELECT count(a) FROM Application a", Long.class).uniqueResult();
    }

    // Used by students to see all the jobs they have applied for
    @Override
    public List<Application> findByStudent_UserId(Long studentUserId) {
        String hql = "FROM Application a JOIN FETCH a.job j JOIN FETCH j.employer WHERE a.student.userId = :userId";
        Query<Application> query = getSession().createQuery(hql, Application.class);
        query.setParameter("userId", studentUserId);
        return query.list();
    }

    // Checks if a specific student has already applied to a specific job (to
    // prevent duplicates)
    @Override
    public Optional<Application> findByStudent_UserIdAndJob_JobId(Long studentUserId, Long jobId) {
        String hql = "FROM Application a WHERE a.student.userId = :userId AND a.job.jobId = :jobId";
        Query<Application> query = getSession().createQuery(hql, Application.class);
        query.setParameter("userId", studentUserId);
        query.setParameter("jobId", jobId);
        return query.uniqueResultOptional();
    }

    // Lists everyone who has applied for a particular job
    @Override
    public List<Application> findByJob_JobId(Long jobId) {
        String hql = "FROM Application a JOIN FETCH a.student WHERE a.job.jobId = :jobId";
        Query<Application> query = getSession().createQuery(hql, Application.class);
        query.setParameter("jobId", jobId);
        return query.list();
    }

    // Filters applications by their current status (e.g., 'Interviewing')
    @Override
    public List<Application> findByStatus(ApplicationStatus status) {
        String hql = "FROM Application a JOIN FETCH a.student JOIN FETCH a.job WHERE a.status = :status";
        Query<Application> query = getSession().createQuery(hql, Application.class);
        query.setParameter("status", status);
        return query.list();
    }

    // Used by Employers to see EVERY application submitted for ANY of their job
    // postings
    @Override
    public List<Application> findByJob_Employer_UserId(Long employerUserId) {
        String hql = "FROM Application a JOIN FETCH a.student JOIN FETCH a.job j JOIN FETCH j.employer WHERE j.employer.userId = :userId";
        Query<Application> query = getSession().createQuery(hql, Application.class);
        query.setParameter("userId", employerUserId);
        return query.list();
    }
}
