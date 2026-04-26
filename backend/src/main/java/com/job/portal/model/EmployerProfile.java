package com.job.portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "EMPLOYER_PROFILES")
public class EmployerProfile implements java.io.Serializable {

    @Id
    @Column(name = "USER_ID")
    private Long userId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "COMPANY_NAME", length = 200)
    private String companyName;

    @Column(name = "INDUSTRY", length = 100)
    private String industry;

    @Column(name = "WEBSITE", length = 500)
    private String website;

    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    @Column(name = "LOCATION", length = 200)
    private String location;

    public EmployerProfile() {}

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
