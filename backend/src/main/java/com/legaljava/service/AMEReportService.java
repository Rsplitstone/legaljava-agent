package com.legaljava.service;

import com.legaljava.entity.AMEReport;
import com.legaljava.entity.WorkersCompCase;
import com.legaljava.repository.AMEReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AMEReportService {

    @Autowired
    private AMEReportRepository ameReportRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String RAG_SERVICE_URL = "http://localhost:8001";

    // Basic CRUD operations
    public List<AMEReport> getAllReports() {
        return ameReportRepository.findAll();
    }

    public Optional<AMEReport> getReportById(Long id) {
        return ameReportRepository.findById(id);
    }

    public AMEReport saveReport(AMEReport ameReport) {
        return ameReportRepository.save(ameReport);
    }

    public void deleteReport(Long id) {
        ameReportRepository.deleteById(id);
    }

    // Business logic methods
    public List<AMEReport> getReportsByCase(WorkersCompCase workersCompCase) {
        return ameReportRepository.findByWorkersCompCase(workersCompCase);
    }

    public List<AMEReport> getReportsByCaseId(Long caseId) {
        return ameReportRepository.findByWorkersCompCaseId(caseId);
    }

    public List<AMEReport> searchReportsByDoctorName(String doctorName) {
        return ameReportRepository.findByDoctorNameContainingIgnoreCase(doctorName);
    }

    public List<AMEReport> getReportsBySpecialty(String specialty) {
        return ameReportRepository.findBySpecialty(specialty);
    }

    public List<AMEReport> getFinalReports(Boolean isFinal) {
        return ameReportRepository.findByIsFinal(isFinal);
    }

    public List<AMEReport> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return ameReportRepository.findByExaminationDateBetween(startDate, endDate);
    }

    public List<AMEReport> getFinalReportsByCase(Long caseId) {
        return ameReportRepository.findFinalReportsByCaseId(caseId);
    }

    public List<AMEReport> getReportsNeedingSummary() {
        return ameReportRepository.findReportsNeedingSummary();
    }

    public List<AMEReport> getReportsByDisabilityRating(BigDecimal minRating) {
        return ameReportRepository.findByDisabilityRatingGreaterThanEqual(minRating);
    }

    /**
     * Create a new AME report
     */
    public AMEReport createReport(WorkersCompCase workersCompCase, String doctorName,
            String specialty, LocalDate examinationDate, String filePath) {
        AMEReport report = new AMEReport(workersCompCase, doctorName, specialty, examinationDate, filePath);
        return ameReportRepository.save(report);
    }

    /**
     * Generate AI summary for an AME report using the Python RAG service
     */
    public AMEReport generateAISummary(Long reportId) {
        Optional<AMEReport> reportOpt = ameReportRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            throw new RuntimeException("AME Report not found with ID: " + reportId);
        }

        AMEReport report = reportOpt.get();

        try {
            // Call Python RAG service for summarization
            String summarizeUrl = RAG_SERVICE_URL + "/summarize_ame_report";

            // Create request payload
            SummarizeRequest request = new SummarizeRequest(report.getReportContent());

            // Call the RAG service
            ResponseEntity<SummarizeResponse> response = restTemplate.postForEntity(
                    summarizeUrl, request, SummarizeResponse.class);

            if (response.getBody() != null) {
                String aiSummary = response.getBody().getSummary();
                BigDecimal disabilityRating = response.getBody().getDisabilityRating();
                String workRestrictions = response.getBody().getWorkRestrictions();
                String treatmentRecommendations = response.getBody().getTreatmentRecommendations();

                // Update the report with AI-generated content
                report.setAiSummary(aiSummary);
                if (disabilityRating != null) {
                    report.setRecommendedDisabilityRating(disabilityRating);
                }
                if (workRestrictions != null) {
                    report.setWorkRestrictions(workRestrictions);
                }
                if (treatmentRecommendations != null) {
                    report.setTreatmentRecommendations(treatmentRecommendations);
                }

                return ameReportRepository.save(report);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AI summary: " + e.getMessage());
        }

        return report;
    }

    /**
     * Batch process all reports needing summaries
     */
    public int generateAISummariesForPendingReports() {
        List<AMEReport> reportsNeedingSummary = getReportsNeedingSummary();
        int processed = 0;

        for (AMEReport report : reportsNeedingSummary) {
            try {
                generateAISummary(report.getId());
                processed++;
            } catch (Exception e) {
                // Log error but continue processing other reports
                System.err.println("Failed to process report ID " + report.getId() + ": " + e.getMessage());
            }
        }

        return processed;
    }

    /**
     * Get comprehensive report analytics
     */
    public AMEReportAnalytics getReportAnalytics() {
        long totalReports = ameReportRepository.count();
        long finalReports = ameReportRepository.findByIsFinal(true).size();
        long reportsWithSummary = ameReportRepository.findAll().stream()
                .mapToInt(r -> r.getAiSummary() != null ? 1 : 0)
                .sum();
        long reportsNeedingSummary = getReportsNeedingSummary().size();

        return new AMEReportAnalytics(totalReports, finalReports, reportsWithSummary, reportsNeedingSummary);
    }

    // DTOs for API communication
    public static class SummarizeRequest {
        private String reportContent;

        public SummarizeRequest(String reportContent) {
            this.reportContent = reportContent;
        }

        public String getReportContent() {
            return reportContent;
        }

        public void setReportContent(String reportContent) {
            this.reportContent = reportContent;
        }
    }

    public static class SummarizeResponse {
        private String summary;
        private BigDecimal disabilityRating;
        private String workRestrictions;
        private String treatmentRecommendations;

        // Getters and setters
        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public BigDecimal getDisabilityRating() {
            return disabilityRating;
        }

        public void setDisabilityRating(BigDecimal disabilityRating) {
            this.disabilityRating = disabilityRating;
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
    }

    // Analytics DTO
    public static class AMEReportAnalytics {
        private final long totalReports;
        private final long finalReports;
        private final long reportsWithSummary;
        private final long reportsNeedingSummary;

        public AMEReportAnalytics(long totalReports, long finalReports, long reportsWithSummary,
                long reportsNeedingSummary) {
            this.totalReports = totalReports;
            this.finalReports = finalReports;
            this.reportsWithSummary = reportsWithSummary;
            this.reportsNeedingSummary = reportsNeedingSummary;
        }

        public long getTotalReports() {
            return totalReports;
        }

        public long getFinalReports() {
            return finalReports;
        }

        public long getReportsWithSummary() {
            return reportsWithSummary;
        }

        public long getReportsNeedingSummary() {
            return reportsNeedingSummary;
        }
    }
}
