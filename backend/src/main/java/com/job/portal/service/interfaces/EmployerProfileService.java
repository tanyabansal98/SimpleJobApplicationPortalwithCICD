package com.job.portal.service.interfaces;

import com.job.portal.model.EmployerProfile;

// Manages company profiles — retrieving and updating employer-facing information.
public interface EmployerProfileService {

    // Loads the employer's company profile, creating a blank one if it doesn't exist yet.
    EmployerProfile getProfile(Long userId);

    // Saves changes to an employer's company name, industry, location, and other details.
    EmployerProfile createOrUpdateProfile(EmployerProfile profile);
}
