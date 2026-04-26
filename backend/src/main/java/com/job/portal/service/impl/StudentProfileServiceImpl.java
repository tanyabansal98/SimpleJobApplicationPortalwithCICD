package com.job.portal.service.impl;

import com.job.portal.model.StudentProfile;
import com.job.portal.model.User;
import com.job.portal.dao.StudentProfileDAO;
import com.job.portal.dao.UserDAO;
import com.job.portal.service.interfaces.StudentProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// Handles loading, updating, and resume uploads for student profiles.
@Service
@Transactional
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileDAO studentProfileDAO;
    private final UserDAO userDAO;

    @Value("${app.upload.dir:/Users/tanyabansal/Desktop/WebDevFinalProject/uploads/resumes}")
    private String uploadDir;

    public StudentProfileServiceImpl(StudentProfileDAO studentProfileDAO,
            UserDAO userDAO) {
        this.studentProfileDAO = studentProfileDAO;
        this.userDAO = userDAO;
    }

    // Title-cases a name so it looks consistent regardless of how the student typed it.
    private String capitalizeName(String name) {
        if (name == null || name.isBlank()) return name;
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

    // Retrieves the student's profile, lazily creating a blank one on first access.
    @Override
    @Transactional(readOnly = true)
    public StudentProfile getProfile(Long userId) {
        // We try to find the profile in the database
        return studentProfileDAO.findById(userId)
                .orElseGet(() -> {
                    // LAZY CREATION: If no profile exists yet, we create a blank one on the fly.
                    // This prevents the application from crashing if a user visits their profile for the first time.
                    User user = userDAO.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    StudentProfile profile = new StudentProfile();
                    profile.setUser(user);
                    return studentProfileDAO.save(profile);
                });
    }

    // Merges the submitted profile fields into the existing record, formatting names along the way.
    @Override
    public StudentProfile createOrUpdateProfile(StudentProfile profile) {

        StudentProfile existing = studentProfileDAO.findById(profile.getUserId())
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profile.getUserId()));

        existing.setFirstName(capitalizeName(profile.getFirstName()));
        existing.setLastName(capitalizeName(profile.getLastName()));
        existing.setUniversity(profile.getUniversity());
        existing.setMajor(profile.getMajor());
        existing.setGraduationYear(profile.getGraduationYear());
        existing.setPhone(profile.getPhone());
        existing.setSkills(profile.getSkills());
        existing.setExperience(profile.getExperience());
        existing.setProjects(profile.getProjects());
        existing.setCertifications(profile.getCertifications());

        return studentProfileDAO.save(existing);
    }

    // Stores the uploaded file on disk and records the filename in the student's profile.
    @Override
    public void uploadResume(Long userId, MultipartFile file) {
        try {
            String fileName = com.job.portal.util.FileStorageUtil.saveResume(userId, file);
            
            StudentProfile profile = getProfile(userId);
            profile.setResumeFileName(fileName);
            profile.setResumeContentType(file.getContentType());
            studentProfileDAO.save(profile);
            
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file: " + e.getMessage());
        }
    }
}
