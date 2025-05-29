package com.legaljava.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "ame_reports")
public class AMEReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private WorkersCompCase workersCompCase;

    @Column(nullable = false)
    private String doctorName;

    @Column(nullable = false)
    private String specialty;

    @Column(nullable = false)
    private LocalDate examinationDate;

    @Column(columnDefinition = "TEXT")
    private String reportContent;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    @Column(precision = 5, scale = 2)
    private BigDecimal recommendedDisabilityRating;

    @Column(columnDefinition = "TEXT")
    private String workRestrictions;

    @Column(columnDefinition = "TEXT")
    private String treatmentRecommendations;

    private Boolean isFinal;

    @Column(nullable = false)
    private String filePath;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public AMEReport() {
    }

    public AMEReport(WorkersCompCase workersCompCase, String doctorName, String specialty,
            LocalDate examinationDate, String filePath) {
        this.workersCompCase = workersCompCase;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.examinationDate = examinationDate;
        this.filePath = filePath;
        this.isFinal = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkersCompCase getWorkersCompCase() {
        return workersCompCase;
    }

    public void setWorkersCompCase(WorkersCompCase workersCompCase) {
        this.workersCompCase = workersCompCase;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public LocalDate getExaminationDate() {
        return examinationDate;
    }

    public void setExaminationDate(LocalDate examinationDate) {
        this.examinationDate = examinationDate;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public BigDecimal getRecommendedDisabilityRating() {
        return recommendedDisabilityRating;
    }

    public void setRecommendedDisabilityRating(BigDecimal recommendedDisabilityRating) {
        this.recommendedDisabilityRating = recommendedDisabilityRating;
    }

    public String getWorkRestrictions() {
        return workRestrictions;
    }

    public void setWorkRestrictions(String workRestrictions) {
        this.workRestrictions = workRestrictions;
    }

    public String getTreatmentRecommendations() {
        return treatmentRecommendations;
    }

    public void setTreatmentRecommendations(String treatmentRecommendations) {
        this.treatmentRecommendations = treatmentRecommendations;
    }

    public Boolean getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
}
