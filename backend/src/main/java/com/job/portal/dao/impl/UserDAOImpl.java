package com.job.portal.dao.impl;

import com.job.portal.dao.UserDAO;
import com.job.portal.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {

    private final SessionFactory sessionFactory;

    public UserDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    // Simple look-up by the primary key (USER_ID)
    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(getSession().find(User.class, userId));
    }

    // Fetches every user registered in the system
    @Override
    public List<User> findAll() {
        return getSession().createQuery("FROM User", User.class).list();
    }

    // Counts how many users we have in total
    @Override
    public long count() {
        return getSession().createQuery("SELECT count(u) FROM User u", Long.class).uniqueResult();
    }

    // This is used for login. We use a parameterized query (:email) to prevent SQL
    // Injection.
    @Override
    public Optional<User> findByEmail(String email) {
        String hql = "FROM User u WHERE u.email = :email";
        Query<User> query = getSession().createQuery(hql, User.class);
        query.setParameter("email", email);
        return query.uniqueResultOptional();
    }

    // Saves a new user or updates an existing one
    @Override
    public User save(User user) {
        if (user.getUserId() == null) {
            // New user, so we persist it
            getSession().persist(user);
        } else {
            // Existing user, so we merge the changes
            user = getSession().merge(user);
        }
        return user;
    }
}
