package com.job.portal.service.impl;

import com.job.portal.service.interfaces.EmbeddingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${embedding.mock.enabled:false}")
    private boolean mockEnabled;

    private static final String OPENAI_EMBEDDINGS_URL = "https://api.openai.com/v1/embeddings";
    private static final String MODEL = "text-embedding-3-small";
    private static final int VECTOR_SIZE = 1536;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public float[] generateEmbedding(String text) {
        if (mockEnabled) {
            return generateMockEmbedding(text);
        }
        return callOpenAiEmbedding(text);
    }

    // Generates a fake but deterministic vector — same input text always produces
    // the same fake vector, so testing/debugging stays consistent without calling OpenAI.
    private float[] generateMockEmbedding(String text) {
        Random random = new Random(text.hashCode());
        float[] vector = new float[VECTOR_SIZE];
        for (int i = 0; i < VECTOR_SIZE; i++) {
            vector[i] = (random.nextFloat() * 2) - 1; // range: -1 to 1, similar to real embeddings
        }
        return vector;
    }

    @SuppressWarnings("unchecked")
    private float[] callOpenAiEmbedding(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "input", text
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(
                OPENAI_EMBEDDINGS_URL, request, Map.class
        );

        if (response == null || !response.containsKey("data")) {
            throw new RuntimeException("OpenAI embeddings response was empty or malformed.");
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        List<Double> embeddingList = (List<Double>) data.get(0).get("embedding");

        float[] embedding = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            embedding[i] = embeddingList.get(i).floatValue();
        }

        return embedding;
    }
}