package com.job.portal.service.interfaces;

// Handles storing and updating vectors in Qdrant, tagged with metadata
// (like jobId or studentId) so search results can be traced back to real records.
public interface QdrantService {

    // Inserts or updates a vector in the given collection, tagged with an ID and extra metadata.
    void upsertVector(String collectionName, Long pointId, float[] vector, java.util.Map<String, Object> payload);

    void deleteVector(String collectionName, Long pointId);
}