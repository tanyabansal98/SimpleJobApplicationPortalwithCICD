package com.job.portal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Configuration
public class ChromaInitializer implements CommandLineRunner {

    @Value("${chroma.url:http://localhost:8000}")
    private String chromaUrl;

    @Value("${chroma.collection-name:job-embeddings}")
    private String collectionName;

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();
        String createCollectionUrl = chromaUrl + "/api/v1/collections";

        Map<String, Object> body = Map.of(
                "name", collectionName,
                "get_or_create", true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(createCollectionUrl, request, String.class);
            System.out.println("✅ Chroma '" + collectionName + "' collection created/verified.");
        } catch (Exception e) {
            System.err.println("⚠️ Chroma collection setup failed: " + e.getMessage());
        }
    }
}