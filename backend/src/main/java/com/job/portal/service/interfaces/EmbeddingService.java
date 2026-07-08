package com.job.portal.service.interfaces;

// Converts any piece of text (resume or job description) into a 1536-number
// vector using OpenAI's embedding model, so it can be compared for similarity in Qdrant.
public interface EmbeddingService {

    // Takes plain text, returns its embedding vector.
    float[] generateEmbedding(String text);
}