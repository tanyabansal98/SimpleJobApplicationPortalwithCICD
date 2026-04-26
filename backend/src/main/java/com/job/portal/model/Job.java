package com.job.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "JOBS")
public class Job implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobs_seq_gen")
    @SequenceGenerator(name = "jobs_seq_gen", sequenceName = "JOBS_SEQ", allocationSize = 1)
    @Column(name = "JOB_ID")
    private Long jobId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYER_USER_ID", nullable = false)
    private User employer;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "LOCATION", length = 200)
    private String location;

    @Column(name = "SALARY_RANGE", length = 100)
    private String salaryRange;

    @Column(name = "REQUIRED_SKILLS", length = 1000)
    private String requiredSkills;

    @Column(name = "DURATION", length = 100)
    private String duration;

    @Column(name = "BENEFITS", length = 1000)
    private String benefits;

    @Column(name = "COMPANY_NAME", length = 200)
    private String companyName;

    @Column(name = "JOB_TYPE", length = 50)
    private String jobType;

    public Job() {}

    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    @Column(name = "DEADLINE")
    private LocalDateTime deadline;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public User getEmployer() { return employer; }
    public void setEmployer(User employer) { this.employer = employer; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFormattedCreatedAt() {
        if (createdAt == null) return "N/A";
        return createdAt.toString().substring(0, 10);
    }

    @JsonProperty("displayCompanyName")
    public String getDisplayCompanyName() {
        if (companyName != null && !companyName.isEmpty()) {
            return companyName;
        }
        if (employer != null && employer.getEmployerProfile() != null) {
            return employer.getEmployerProfile().getCompanyName();
        }
        return "Unknown Company";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.active == null)
            this.active = true;
    }

    // Builder
    public static JobBuilder builder() { return new JobBuilder(); }

    public static class JobBuilder {
        private User employer;
        private String title;
        private String description;
        private String location;
        private String salaryRange;
        private String requiredSkills;
        private String duration;
        private String benefits;
        private Boolean active;

        public JobBuilder employer(User employer) { this.employer = employer; return this; }
        public JobBuilder title(String title) { this.title = title; return this; }
        public JobBuilder description(String description) { this.description = description; return this; }
        public JobBuilder location(String location) { this.location = location; return this; }
        public JobBuilder salaryRange(String salaryRange) { this.salaryRange = salaryRange; return this; }
        public JobBuilder requiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; return this; }
        public JobBuilder duration(String duration) { this.duration = duration; return this; }
        public JobBuilder benefits(String benefits) { this.benefits = benefits; return this; }
        public JobBuilder active(Boolean active) { this.active = active; return this; }

        public Job build() {
            Job job = new Job();
            job.setEmployer(employer);
            job.setTitle(title);
            job.setDescription(description);
            job.setLocation(location);
            job.setSalaryRange(salaryRange);
            job.setRequiredSkills(requiredSkills);
            job.setDuration(duration);
            job.setBenefits(benefits);
            job.setActive(active);
            return job;
        }
    }
}
