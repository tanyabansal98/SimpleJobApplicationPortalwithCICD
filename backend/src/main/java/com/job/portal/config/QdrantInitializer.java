package com.job.portal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Configuration
public class QdrantInitializer implements CommandLineRunner {

    @Value("${qdrant.url:http://localhost:6333}")
    private String qdrantUrl;

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();
        String collectionUrl = qdrantUrl + "/collections/jobs";

        Map<String, Object> vectorsConfig = Map.of(
            "size", 1536,
            "distance", "Cosine"
        );
        Map<String, Object> body = Map.of("vectors", vectorsConfig);

        try {
            restTemplate.put(collectionUrl, body);
            System.out.println("✅ Qdrant 'jobs' collection created/verified.");
        } catch (Exception e) {
            System.err.println("⚠️ Qdrant collection setup failed: " + e.getMessage());
        }
    }
}