package com.job.portal.service.interfaces;

import com.job.portal.model.StudentProfile;
import org.springframework.web.multipart.MultipartFile;

// Manages student profile data including personal info, skills, and resume uploads.
public interface StudentProfileService {

    // Loads a student's profile, auto-creating a blank one if they haven't filled
    // it in yet.
    StudentProfile getProfile(Long userId);

    // Saves updates to a student's name, university, skills, experience, and other
    // details.
    StudentProfile createOrUpdateProfile(StudentProfile profile);

    // Handles the file upload and stores the resume path against the student's
    // profile.
    void uploadResume(Long userId, MultipartFile file);
}
