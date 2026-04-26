package com.job.portal.dao;

import com.job.portal.model.StudentProfile;
import java.util.Optional;

public interface StudentProfileDAO {

    // Looks up a student's profile by their User ID.
    Optional<StudentProfile> findById(Long userId);

    // Creates a new student profile or saves updates (e.g., resume upload).
    StudentProfile save(StudentProfile profile);
}
