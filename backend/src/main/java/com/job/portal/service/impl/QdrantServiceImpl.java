package com.job.portal.service.impl;

import com.job.portal.service.interfaces.QdrantService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class QdrantServiceImpl implements QdrantService {

    @Value("${qdrant.url:http://localhost:6333}")
    private String qdrantUrl;

    @Value("${qdrant.api.key:}")
    private String qdrantApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Builds headers shared by every Qdrant request — adds the api-key header
    // only if one is actually configured (local dev has none, Azure/cloud does).
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (qdrantApiKey != null && !qdrantApiKey.isBlank()) {
            headers.set("api-key", qdrantApiKey);
        }
        return headers;
    }

    @Override
    public void upsertVector(String collectionName, Long pointId, float[] vector, Map<String, Object> payload) {
        String url = qdrantUrl + "/collections/" + collectionName + "/points";

        List<Float> vectorList = new java.util.ArrayList<>(vector.length);
        for (float v : vector) {
            vectorList.add(v);
        }

        Map<String, Object> point = Map.of(
                "id", pointId,
                "vector", vectorList,
                "payload", payload
        );

        Map<String, Object> requestBody = Map.of(
                "points", List.of(point)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, buildHeaders());

        try {
            restTemplate.put(url, request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upsert vector into Qdrant collection '" + collectionName + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteVector(String collectionName, Long pointId) {
        String url = qdrantUrl + "/collections/" + collectionName + "/points";

        Map<String, Object> requestBody = Map.of(
                "ids", List.of(pointId)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, buildHeaders());

        try {
            restTemplate.exchange(url, org.springframework.http.HttpMethod.DELETE, request, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete vector from Qdrant collection '" + collectionName + "': " + e.getMessage(), e);
        }
    }
}