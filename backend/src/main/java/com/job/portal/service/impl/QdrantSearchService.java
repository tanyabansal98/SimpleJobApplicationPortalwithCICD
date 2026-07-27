package com.job.portal.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class QdrantSearchService {

    @Value("${qdrant.url:http://qdrant:6333}")
    private String qdrantUrl;

    @Value("${qdrant.api.key:}")
    private String qdrantApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<Long, Double> searchSimilarJobIds(float[] queryVector, int topN) {
        String searchUrl = qdrantUrl + "/collections/jobs/points/search";

        Map<String, Object> body = new HashMap<>();
        body.put("vector", queryVector);
        body.put("limit", topN);
        body.put("with_payload", true);

        HttpHeaders headers = new HttpHeaders();
        if (qdrantApiKey != null && !qdrantApiKey.isBlank()) {
            headers.set("api-key", qdrantApiKey);
        }
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        Map<String, Object> response = restTemplate.postForObject(searchUrl, request, Map.class);

        Map<Long, Double> jobIdToScore = new LinkedHashMap<>();
        if (response == null || response.get("result") == null) {
            return jobIdToScore;
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("result");
        for (Map<String, Object> result : results) {
            Map<String, Object> payload = (Map<String, Object>) result.get("payload");
            Long jobId = Long.valueOf(payload.get("jobId").toString());
            double score = ((Number) result.get("score")).doubleValue();
            jobIdToScore.put(jobId, score);
        }
        return jobIdToScore;
    }
}