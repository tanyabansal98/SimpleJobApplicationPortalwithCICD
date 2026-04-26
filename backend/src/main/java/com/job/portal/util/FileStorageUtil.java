package com.job.portal.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileStorageUtil {

    private static final String UPLOAD_DIR = "/Users/tanyabansal/Desktop/WebDevFinalProject/uploads/resumes";

    // This is the core function that saves a resume to the server's disk
    public static String saveResume(Long userId, MultipartFile file) throws IOException {
        // First, basic safety checks
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Filename is null");
        }

        // We only allow PDF and DOCX for resumes
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!extension.equals(".pdf") && !extension.equals(".docx")) {
            throw new IllegalArgumentException("Only PDF and DOCX files are allowed.");
        }

        // Keep files under 5MB to save server space
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB limit.");
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // CRITICAL: Before saving a new resume, we search for and delete any old ones 
        // belonging to this user so we don't waste disk space with duplicates.
        try (java.util.stream.Stream<Path> files = Files.list(uploadPath)) {
            files.filter(p -> p.getFileName().toString().startsWith(userId + "_resume."))
                 .forEach(p -> {
                     try {
                         Files.delete(p);
                     } catch (IOException e) {
                         // ignore if already gone
                     }
                 });
        }

        // We name the file using the userId to ensure it's unique and easy to find
        String fileName = userId + "_resume" + extension;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public static Path getResumePath(String fileName) {
        return Paths.get(UPLOAD_DIR).resolve(fileName);
    }
}
