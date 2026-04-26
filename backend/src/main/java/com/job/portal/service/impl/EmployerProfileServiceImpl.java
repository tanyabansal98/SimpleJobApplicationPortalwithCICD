package com.job.portal.service.impl;

import com.job.portal.model.EmployerProfile;
import com.job.portal.model.User;
import com.job.portal.dao.EmployerProfileDAO;
import com.job.portal.dao.UserDAO;
import com.job.portal.service.interfaces.EmployerProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Handles loading and saving employer company profiles.
@Service
@Transactional
public class EmployerProfileServiceImpl implements EmployerProfileService {

    private final EmployerProfileDAO employerProfileDAO;
    private final UserDAO userDAO;

    public EmployerProfileServiceImpl(EmployerProfileDAO employerProfileDAO,
            UserDAO userDAO) {
        this.employerProfileDAO = employerProfileDAO;
        this.userDAO = userDAO;
    }

    // Formats a company name to title-case.
    private String capitalizeName(String name) {
        if (name == null || name.isBlank())
            return name;
        String[] words = name.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (sb.length() > 0)
                    sb.append(' ');
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    // Loads the profile, or auto-creates a blank one the first time an employer
    // visits their profile page.
    @Override
    public EmployerProfile getProfile(Long userId) {
        // Similar to the student profile, we lazily create this if it doesn't exist yet
        return employerProfileDAO.findById(userId)
                .orElseGet(() -> {
                    User user = userDAO.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    EmployerProfile profile = new EmployerProfile();
                    profile.setUser(user);
                    return employerProfileDAO.save(profile);
                });
    }

    // Applies the employer's submitted changes to their existing profile record and
    // saves them.
    @Override
    public EmployerProfile createOrUpdateProfile(EmployerProfile profile) {
        // Find the existing record first to ensure we aren't creating duplicates
        EmployerProfile existing = employerProfileDAO.findById(profile.getUserId())
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profile.getUserId()));

        // Apply the cleaned-up company name and other details
        existing.setCompanyName(capitalizeName(profile.getCompanyName()));
        existing.setIndustry(profile.getIndustry());
        existing.setWebsite(profile.getWebsite());
        existing.setDescription(profile.getDescription());
        existing.setLocation(profile.getLocation());

        return employerProfileDAO.save(existing);
    }
}
