package com.job.portal.service.impl;

import com.job.portal.model.User;
import com.job.portal.model.enums.Role;
import com.job.portal.dao.UserDAO;
import com.job.portal.service.interfaces.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Core implementation for user accounts — registration, authentication, and lifecycle management.
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final com.job.portal.dao.StudentProfileDAO studentProfileDAO;
    private final com.job.portal.dao.EmployerProfileDAO employerProfileDAO;

    public UserServiceImpl(UserDAO userDAO, 
                           PasswordEncoder passwordEncoder,
                           com.job.portal.dao.StudentProfileDAO studentProfileDAO,
                           com.job.portal.dao.EmployerProfileDAO employerProfileDAO) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.studentProfileDAO = studentProfileDAO;
        this.employerProfileDAO = employerProfileDAO;
    }

    // Simple null/blank check used to guard required fields before we process them.
    private String validate(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Input must not be blank.";
        }
        return null;
    }

    // Basic regex check to catch obviously malformed email addresses before they hit the database.
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // Validates input, checks for duplicate emails, hashes the password, saves the user, and creates their profile.
    @Override
    public User register(String email, String password, Role role) {

        // 1. Basic email validation
        String emailError = validate(email);
        if (emailError != null)
            throw new IllegalArgumentException(emailError);

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        // 2. Clean up the email string
        email = email.trim().toLowerCase();

        // 3. Make sure the email isn't already in use
        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email '" + email + "' is already registered.");
        }

        // 4. Ensure the password is strong enough (at least 6 characters)
        if (password == null || password.trim().isEmpty() || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        // 5. Build the user object and securely hash the password using BCrypt
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .active(true)
                .build();

        User saved = userDAO.save(user);
        
        // 6. AUTO-CREATE PROFILE: As soon as a user registers, we create a blank 
        // student or employer profile for them so it's ready when they log in.
        if (role == Role.STUDENT) {
            com.job.portal.model.StudentProfile profile = new com.job.portal.model.StudentProfile();
            profile.setUser(saved);
            studentProfileDAO.save(profile);
        } else if (role == Role.EMPLOYER) {
            com.job.portal.model.EmployerProfile profile = new com.job.portal.model.EmployerProfile();
            profile.setUser(saved);
            employerProfileDAO.save(profile);
        }

        return saved;
    }

    // Looks up the user by email, checks if their account is active, then verifies the password hash.
    @Override
    public User login(String email, String password) {
        User user = findByEmail(email);

        // Check if the admin has deactivated this account
        if (!user.getActive()) {
            throw new RuntimeException("Account is deactivated.");
        }

        // Verify the provided password against the hashed one in the database
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        } else {
            throw new RuntimeException("Invalid email or password.");
        }
    }

    // Trims and lowercases the email before querying to avoid case-sensitivity issues.
    @Override
    public User findByEmail(String email) {
        return userDAO.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // Fetches a user by their ID or throws a descriptive error if they don't exist.
    @Override
    public User findById(Long userId) {
        return userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // Returns all users — used by the admin to see everyone on the platform at once.
    @Override
    public List<User> listAll() {
        return userDAO.findAll();
    }

    // Flips the user's active flag to false so they can't log in until re-enabled.
    @Override
    public void deactivateUser(Long userId) {
        User user = findById(userId);
        user.setActive(false);
        userDAO.save(user);
    }

    // Restores login access by flipping the active flag back to true.
    @Override
    public void activateUser(Long userId) {
        User user = findById(userId);
        user.setActive(true);
        userDAO.save(user);
    }

    // Finds the user by email and replaces their stored password hash with the new one.
    @Override
    public boolean resetPassword(String email, String newPassword) {
        User user = userDAO.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (user == null) {
            return false;
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userDAO.save(user);
        return true;
    }
}
