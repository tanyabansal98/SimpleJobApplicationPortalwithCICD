package com.job.portal.service.interfaces;

import com.job.portal.model.User;

import java.util.List;
import java.util.Map;

// Admin-only operations for managing users and viewing platform-wide stats.
public interface AdminService {

    // Returns a list of every user registered on the portal.
    List<User> listAllUsers();

    // Bans a user from logging in by marking their account as inactive.
    void deactivateUser(Long userId);

    // Re-enables a previously deactivated user account.
    void activateUser(Long userId);

    // Pulls together headline numbers (total users, jobs, applications) for the admin dashboard.
    Map<String, Object> getDashboardStats();
}
