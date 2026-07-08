package com.job.portal.util;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

// Pulls plain text out of an uploaded resume file (PDF, DOCX, etc.)
// so it can later be turned into an embedding vector for job matching.
@Component
public class ResumeTextExtractor {

    private final Tika tika = new Tika();

    public String extractText(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.parseToString(inputStream);
        } catch (TikaException e) {
            throw new IOException("Failed to extract text from resume: " + e.getMessage(), e);
        }
    }
}