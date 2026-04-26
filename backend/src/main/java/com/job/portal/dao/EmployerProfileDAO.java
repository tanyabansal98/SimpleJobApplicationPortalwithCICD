package com.job.portal.dao;

import com.job.portal.model.EmployerProfile;
import java.util.Optional;

public interface EmployerProfileDAO {

    // Looks up an employer's company profile by their User ID.
    Optional<EmployerProfile> findById(Long userId);

    // Creates a new company profile or updates an existing one.
    EmployerProfile save(EmployerProfile profile);
}
