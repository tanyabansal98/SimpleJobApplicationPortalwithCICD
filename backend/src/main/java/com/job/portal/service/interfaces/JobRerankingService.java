package com.job.portal.service.interfaces;

import com.job.portal.dto.JobMatchDTO;
import java.util.List;

// Takes the shortlist of jobs Qdrant found and asks an LLM to re-rank them
// against the resume, adding a human-readable one-sentence reason for each.
public interface JobRerankingService {
    List<JobMatchDTO> rerank(String resumeText, List<JobMatchDTO> candidateJobs);
}