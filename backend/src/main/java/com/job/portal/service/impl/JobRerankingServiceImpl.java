package com.job.portal.service.impl;

import com.job.portal.dto.JobMatchDTO;
import com.job.portal.service.interfaces.JobRerankingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class JobRerankingServiceImpl implements JobRerankingService {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    public List<JobMatchDTO> rerank(String resumeText, List<JobMatchDTO> candidateJobs) {
        if (candidateJobs.isEmpty()) {
            return candidateJobs;
        }

        String prompt = buildPrompt(resumeText, candidateJobs);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response;
        try {
            response = restTemplate.postForObject(OPENAI_CHAT_URL, request, Map.class);
        } catch (Exception e) {
            System.err.println("❌ OpenAI chat call failed: " + e.getMessage());
            e.printStackTrace();
            return candidateJobs;
        }

        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");

            return applyRanking(content, candidateJobs);
        } catch (Exception e) {
            System.err.println("❌ Failed to parse/apply LLM ranking: " + e.getMessage());
            e.printStackTrace();
            return candidateJobs;
        }
    }

    // Builds one prompt containing the resume and every candidate job, asking for
    // a ranked JSON array back so we can parse it reliably.
    private String buildPrompt(String resumeText, List<JobMatchDTO> jobs) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are helping rank job matches for a student based on their resume.\n\n");
        sb.append("RESUME:\n").append(resumeText).append("\n\n");
        sb.append("CANDIDATE JOBS:\n");
        for (JobMatchDTO job : jobs) {
            sb.append("jobId: ").append(job.getJobId())
              .append(", title: ").append(job.getTitle())
              .append(", description: ").append(job.getDescription())
              .append(", requiredSkills: ").append(job.getRequiredSkills())
              .append("\n");
        }
        sb.append("\nRank these jobs from best to worst match for this candidate. ");
        sb.append("Respond with ONLY a JSON array, no other text, in this exact format:\n");
        sb.append("[{\"jobId\": 5, \"reason\": \"one sentence reason\"}, {\"jobId\": 9, \"reason\": \"...\"}]");
        return sb.toString();
    }

    // Parses the LLM's JSON ranking and reorders/annotates the original DTOs to match.
    private List<JobMatchDTO> applyRanking(String llmResponseContent, List<JobMatchDTO> originalJobs) throws Exception {
        // Strip markdown code fences in case the model wraps its JSON in ```json ... ```
        String cleaned = llmResponseContent.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("```json", "").replaceAll("```", "").trim();
        }

        List<Map<String, Object>> ranking = objectMapper.readValue(cleaned, List.class);

        Map<Long, JobMatchDTO> jobsById = new HashMap<>();
        for (JobMatchDTO job : originalJobs) {
            jobsById.put(job.getJobId(), job);
        }

        List<JobMatchDTO> reranked = new ArrayList<>();
        for (Map<String, Object> entry : ranking) {
            Long jobId = Long.valueOf(entry.get("jobId").toString());
            String reason = (String) entry.get("reason");

            JobMatchDTO original = jobsById.get(jobId);
            if (original != null) {
                original.setReason(reason);
                reranked.add(original);
                jobsById.remove(jobId);
            }
        }

        // Any jobs the LLM didn't mention (shouldn't normally happen) get appended at the end,
        // so we never silently drop a job from the response.
        reranked.addAll(jobsById.values());

        return reranked;
    }
}