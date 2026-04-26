package com.job.portal.service.interfaces;

import com.job.portal.model.User;
import com.job.portal.model.enums.Role;

import java.util.List;

// Core user account operations — registration, login, lookup, and account management.
public interface UserService {

    // Registers a new user, validates their email and password, hashes the password, and auto-creates their profile.
    User register(String email, String password, Role role);

    // Authenticates a user by checking their email and bcrypt-hashed password.
    User login(String email, String password);

    // Looks up a user account by their email address.
    User findByEmail(String email);

    // Looks up a user account by their numeric ID.
    User findById(Long userId);

    // Returns every user registered on the platform.
    List<User> listAll();

    // Disables a user account so they can no longer log in.
    void deactivateUser(Long userId);

    // Re-enables a previously disabled user account.
    void activateUser(Long userId);

    // Updates a user's password after verifying their email exists in the system.
    boolean resetPassword(String email, String newPassword);
}
