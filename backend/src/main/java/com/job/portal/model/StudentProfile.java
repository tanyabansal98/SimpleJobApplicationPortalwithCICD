package com.job.portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "STUDENT_PROFILES")
public class StudentProfile implements java.io.Serializable {

    @Id
    @Column(name = "USER_ID")
    private Long userId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "FIRST_NAME", length = 100)
    private String firstName;

    @Column(name = "LAST_NAME", length = 100)
    private String lastName;

    @Column(name = "UNIVERSITY", length = 200)
    private String university;

    @Column(name = "MAJOR", length = 100)
    private String major;

    @Column(name = "GRADUATION_YEAR")
    private Integer graduationYear;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "SKILLS", length = 2000)
    private String skills;

    @Column(name = "EXPERIENCE", length = 4000)
    private String experience;

    @Column(name = "PROJECTS", length = 4000)
    private String projects;

    @Column(name = "CERTIFICATIONS", length = 2000)
    private String certifications;

    @Column(name = "RESUME_FILE_NAME", length = 255)
    private String resumeFileName;

    @Column(name = "RESUME_CONTENT_TYPE", length = 100)
    private String resumeContentType;

    public StudentProfile() {}

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }

    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }

    public String getResumeContentType() { return resumeContentType; }
    public void setResumeContentType(String resumeContentType) { this.resumeContentType = resumeContentType; }
}
