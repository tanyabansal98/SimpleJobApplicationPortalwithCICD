package com.job.portal.auth.repository;

import com.job.portal.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository is our Repository layer.
 * Analogy: A smart database assistant.
 * We simply extend JpaRepository, and Spring Boot generates all the standard 
 * database queries (like findById, save, delete) automatically.
 *
 * @Repository: Tells Spring Boot that this interface is responsible for database access.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * 
     * Spring Data JPA reads the method name "findByEmail" and automatically writes 
     * the SQL query under the hood: "SELECT * FROM USERS WHERE EMAIL = ?"
     * 
     * @param email The user email
     * @return An Optional containing the User if found, or empty if not
     */
    Optional<User> findByEmail(String email);
}
