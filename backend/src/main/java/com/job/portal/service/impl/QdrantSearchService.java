package com.job.portal.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class QdrantSearchService {

    @Value("${qdrant.url:http://qdrant:6333}")
    private String qdrantUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Returns raw match results as jobId -> similarity score.
    // Job details are deliberately NOT read here — Qdrant is only used to find
    // WHICH jobs match; the actual data always comes fresh from Postgres.
    public Map<Long, Double> searchSimilarJobIds(float[] queryVector, int topN) {
        String searchUrl = qdrantUrl + "/collections/jobs/points/search";

        Map<String, Object> body = new HashMap<>();
        body.put("vector", queryVector);
        body.put("limit", topN);
        body.put("with_payload", true);

        Map<String, Object> response = restTemplate.postForObject(searchUrl, body, Map.class);

        Map<Long, Double> jobIdToScore = new LinkedHashMap<>(); // preserves Qdrant's ranking order
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