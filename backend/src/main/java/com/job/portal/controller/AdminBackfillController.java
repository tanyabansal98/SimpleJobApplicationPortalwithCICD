package com.job.portal.controller;

import com.job.portal.service.interfaces.JobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// TEMPORARY controller — only exists to trigger the one-time job embedding backfill.
// Delete this whole file once the backfill has been run successfully.
@RestController
public class AdminBackfillController {

    private final JobService jobService;

    public AdminBackfillController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/admin/backfill-jobs")
    @ResponseBody
    public String backfillJobs() {
        int count = jobService.backfillJobEmbeddings();
        return "Embedded " + count + " jobs into Qdrant.";
    }
}