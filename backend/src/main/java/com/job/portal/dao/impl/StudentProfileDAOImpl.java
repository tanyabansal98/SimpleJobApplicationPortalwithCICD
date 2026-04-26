package com.job.portal.dao.impl;

import com.job.portal.dao.StudentProfileDAO;
import com.job.portal.model.StudentProfile;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class StudentProfileDAOImpl implements StudentProfileDAO {

    private final SessionFactory sessionFactory;

    public StudentProfileDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // Finds a student's profile information using their unique User ID
    @Override
    public Optional<StudentProfile> findById(Long userId) {
        return Optional.ofNullable(getSession().find(StudentProfile.class, userId));
    }

    // This is called whenever we save a student's resume info or profile updates to the database
    @Override
    public StudentProfile save(StudentProfile profile) {
        if (profile.getUserId() == null) {
            // New profile being created
            getSession().persist(profile);
        } else {
            // Updating an existing profile
            profile = getSession().merge(profile);
        }
        return profile;
    }
}
