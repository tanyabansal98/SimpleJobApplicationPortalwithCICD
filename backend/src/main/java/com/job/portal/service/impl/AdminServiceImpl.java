package com.job.portal.service.impl;

import com.job.portal.model.User;
import com.job.portal.dao.ApplicationDAO;
import com.job.portal.dao.JobDAO;
import com.job.portal.dao.UserDAO;
import com.job.portal.service.interfaces.AdminService;
import com.job.portal.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final UserDAO userDAO;
    private final JobDAO jobDAO;
    private final ApplicationDAO applicationDAO;

    public AdminServiceImpl(UserService userService,
            UserDAO userDAO,
            JobDAO jobDAO,
            ApplicationDAO applicationDAO) {
        this.userService = userService;
        this.userDAO = userDAO;
        this.jobDAO = jobDAO;
        this.applicationDAO = applicationDAO;
    }

    // Delegates to UserService to return all registered accounts.
    @Override
    public List<User> listAllUsers() {
        return userService.listAll();
    }

    // Passes through to UserService to disable the account.
    @Override
    public void deactivateUser(Long userId) {
        userService.deactivateUser(userId);
    }

    // Passes through to UserService to re-enable the account.
    @Override
    public void activateUser(Long userId) {
        userService.activateUser(userId);
    }

    // Queries the DAOs directly to build a quick summary map for the admin overview
    // page.
    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userDAO.count());
        stats.put("totalJobs", jobDAO.count());
        stats.put("totalApplications", applicationDAO.count());
        stats.put("activeJobs", jobDAO.findByActiveTrue().size());
        return stats;
    }
}
