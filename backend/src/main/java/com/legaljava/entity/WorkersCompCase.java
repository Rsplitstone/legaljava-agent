package com.legaljava.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "workers_comp_cases")
public class WorkersCompCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String caseNumber;

    @Column(nullable = false)
    private String claimantName;

    @Column(nullable = false)
    private String employerName;

    @Column(nullable = false)
    private LocalDate injuryDate;

    @Column(nullable = false)
    private String injuryDescription;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    private String adjusterName;
    private String adjusterId;

    @Column(precision = 10, scale = 2)
    private BigDecimal weeklyWage;

    @Column(precision = 5, scale = 2)
    private BigDecimal disabilityRating;

    private LocalDate maxMedicalImprovement;

    @Column(columnDefinition = "TEXT")
    private String caseNotes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public WorkersCompCase() {
    }

    public WorkersCompCase(String caseNumber, String claimantName, String employerName,
            LocalDate injuryDate, String injuryDescription) {
        this.caseNumber = caseNumber;
        this.claimantName = claimantName;
        this.employerName = employerName;
        this.injuryDate = injuryDate;
        this.injuryDescription = injuryDescription;
        this.status = CaseStatus.OPEN;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public LocalDate getInjuryDate() {
        return injuryDate;
    }

    public void setInjuryDate(LocalDate injuryDate) {
        this.injuryDate = injuryDate;
    }

    public String getInjuryDescription() {
        return injuryDescription;
    }

    public void setInjuryDescription(String injuryDescription) {
        this.injuryDescription = injuryDescription;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public String getAdjusterName() {
        return adjusterName;
    }

    public void setAdjusterName(String adjusterName) {
        this.adjusterName = adjusterName;
    }

    public String getAdjusterId() {
        return adjusterId;
    }

    public void setAdjusterId(String adjusterId) {
        this.adjusterId = adjusterId;
    }

    public BigDecimal getWeeklyWage() {
        return weeklyWage;
    }

    public void setWeeklyWage(BigDecimal weeklyWage) {
        this.weeklyWage = weeklyWage;
    }

    public BigDecimal getDisabilityRating() {
        return disabilityRating;
    }

    public void setDisabilityRating(BigDecimal disabilityRating) {
        this.disabilityRating = disabilityRating;
    }

    public LocalDate getMaxMedicalImprovement() {
        return maxMedicalImprovement;
    }

    public void setMaxMedicalImprovement(LocalDate maxMedicalImprovement) {
        this.maxMedicalImprovement = maxMedicalImprovement;
    }

    public String getCaseNotes() {
        return caseNotes;
    }

    public void setCaseNotes(String caseNotes) {
        this.caseNotes = caseNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public enum CaseStatus {
        OPEN, PENDING_REVIEW, CLOSED, SETTLED, LITIGATED
    }
}
