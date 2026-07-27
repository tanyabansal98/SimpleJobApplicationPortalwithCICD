package com.job.portal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Configuration
public class QdrantInitializer implements CommandLineRunner {

    @Value("${qdrant.url:http://localhost:6333}")
    private String qdrantUrl;

    @Value("${qdrant.api.key:}")
    private String qdrantApiKey;

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();
        String collectionUrl = qdrantUrl + "/collections/jobs";

        Map<String, Object> vectorsConfig = Map.of(
            "size", 1536,
            "distance", "Cosine"
        );
        Map<String, Object> body = Map.of("vectors", vectorsConfig);

        HttpHeaders headers = new HttpHeaders();
        if (qdrantApiKey != null && !qdrantApiKey.isBlank()) {
            headers.set("api-key", qdrantApiKey);
        }
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.put(collectionUrl, request);
            System.out.println("✅ Qdrant 'jobs' collection created/verified.");
        } catch (Exception e) {
            System.err.println("⚠️ Qdrant collection setup failed: " + e.getMessage());
        }
    }
}