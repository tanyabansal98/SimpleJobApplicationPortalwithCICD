package com.job.portal.dao.impl;

import com.job.portal.dao.EmployerProfileDAO;
import com.job.portal.model.EmployerProfile;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class EmployerProfileDAOImpl implements EmployerProfileDAO {

    private final SessionFactory sessionFactory;

    public EmployerProfileDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // Look up a company's profile using the Employer's unique User ID
    @Override
    public Optional<EmployerProfile> findById(Long userId) {
        return Optional.ofNullable(getSession().find(EmployerProfile.class, userId));
    }

    // This method is called to either create a new profile or update existing
    // company info
    @Override
    public EmployerProfile save(EmployerProfile profile) {
        if (profile.getUserId() == null) {
            // No ID yet, so it's a new record
            getSession().persist(profile);
        } else {
            // ID exists, so we are updating an existing profile
            profile = getSession().merge(profile);
        }
        return profile;
    }
}
