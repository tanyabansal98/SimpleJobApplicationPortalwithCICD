package com.job.portal.dao;

import com.job.portal.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {

    // Looks up a user by their primary key (User ID).
    Optional<User> findById(Long userId);

    // Returns every registered user in the system.
    List<User> findAll();

    // Returns the total number of registered users.
    long count();

    // Finds a user by email — the main lookup used during login.
    Optional<User> findByEmail(String email);

    // Persists a new user account or saves changes to an existing one.
    User save(User user);
}
