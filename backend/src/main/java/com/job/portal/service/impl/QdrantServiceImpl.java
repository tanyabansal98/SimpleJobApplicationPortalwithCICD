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

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void upsertVector(String collectionName, Long pointId, float[] vector, Map<String, Object> payload) {
        String url = qdrantUrl + "/collections/" + collectionName + "/points";

        // Qdrant expects vectors as a plain list of numbers, not a Java float[].
        // We convert here so the JSON we send matches what Qdrant's API expects.
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // RestTemplate doesn't have a delete method that takes a body, so we use exchange.
            // We expect 200 OK or 202 Accepted on success.
            restTemplate.exchange(url, org.springframework.http.HttpMethod.DELETE, request, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete vector from Qdrant collection '" + collectionName + "': " + e.getMessage(), e);
        }
    }
}