package com.job.portal.util;

import com.fasterxml.jackson.databind.ObjectMapper;

// Converts embedding vectors between Java's float[] and a JSON string,
// since databases don't have a native "array of 1536 floats" column type.
public class EmbeddingUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Turns a float[] into a JSON string like "[0.012,-0.045,...]" for storage.
    public static String toJson(float[] vector) {
        try {
            return objectMapper.writeValueAsString(vector);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize embedding vector: " + e.getMessage(), e);
        }
    }

    // Turns a stored JSON string back into a usable float[] array.
    public static float[] fromJson(String json) {
        try {
            return objectMapper.readValue(json, float[].class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize embedding vector: " + e.getMessage(), e);
        }
    }
}