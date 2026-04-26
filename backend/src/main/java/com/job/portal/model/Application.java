package com.job.portal.model;

import com.job.portal.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "APPLICATIONS", uniqueConstraints = @UniqueConstraint(name = "UK_APP_STUDENT_JOB", columnNames = {
        "STUDENT_USER_ID", "JOB_ID" }))
public class Application implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apps_seq_gen")
    @SequenceGenerator(name = "apps_seq_gen", sequenceName = "APPLICATIONS_SEQ", allocationSize = 1)
    @Column(name = "APPLICATION_ID")
    private Long applicationId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_USER_ID", nullable = false)
    private User student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 30)
    private ApplicationStatus status;

    @Column(name = "APPLIED_AT", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "EMPLOYER_NOTES", length = 2000)
    private String employerNotes;

    public Application() {}

    // Getters and Setters
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getEmployerNotes() { return employerNotes; }
    public void setEmployerNotes(String employerNotes) { this.employerNotes = employerNotes; }

    public String getFormattedAppliedAt() {
        if (appliedAt == null) return "N/A";
        return appliedAt.toString().substring(0, 10);
    }

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null)
            this.status = ApplicationStatus.SUBMITTED;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Builder
    public static ApplicationBuilder builder() { return new ApplicationBuilder(); }

    public static class ApplicationBuilder {
        private User student;
        private Job job;
        private ApplicationStatus status;

        public ApplicationBuilder student(User student) { this.student = student; return this; }
        public ApplicationBuilder job(Job job) { this.job = job; return this; }
        public ApplicationBuilder status(ApplicationStatus status) { this.status = status; return this; }

        public Application build() {
            Application app = new Application();
            app.setStudent(student);
            app.setJob(job);
            app.setStatus(status);
            return app;
        }
    }
}
