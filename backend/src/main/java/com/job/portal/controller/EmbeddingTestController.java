package com.job.portal.controller;

import com.job.portal.service.interfaces.EmbeddingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// TEMPORARY controller — only exists to manually verify EmbeddingService works.
// Safe to delete once confirmed.
@RestController
@RequestMapping("/api/test")
public class EmbeddingTestController {

    private final EmbeddingService embeddingService;

    public EmbeddingTestController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @GetMapping("/embedding")
    public Map<String, Object> testEmbedding() {
        float[] vector = embeddingService.generateEmbedding(
                "Software engineer with Java and Spring Boot experience"
        );

        Map<String, Object> response = new HashMap<>();
        response.put("length", vector.length);
        response.put("firstFive", new float[]{vector[0], vector[1], vector[2], vector[3], vector[4]});

        return response;
    }
}