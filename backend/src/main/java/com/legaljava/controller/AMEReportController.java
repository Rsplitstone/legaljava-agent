package com.legaljava.controller;

import com.legaljava.entity.AMEReport;
import com.legaljava.entity.WorkersCompCase;
import com.legaljava.service.AMEReportService;
import com.legaljava.service.WorkersCompCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ame-reports")
@CrossOrigin(origins = "*")
@Tag(name = "AME Reports", description = "Agreed Medical Examiner report management endpoints")
public class AMEReportController {

    @Autowired
    private AMEReportService ameReportService;

    @Autowired
    private WorkersCompCaseService workersCompCaseService;

    @GetMapping
    @Operation(summary = "Get all AME reports", description = "Retrieve all AME reports")
    public ResponseEntity<List<AMEReport>> getAllReports() {
        List<AMEReport> reports = ameReportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get AME report by ID", description = "Retrieve a specific AME report by ID")
    public ResponseEntity<AMEReport> getReportById(@PathVariable Long id) {
        Optional<AMEReport> reportOpt = ameReportService.getReportById(id);
        return reportOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new AME report", description = "Create a new AME report")
    public ResponseEntity<AMEReport> createReport(@Valid @RequestBody AMEReport ameReport) {
        AMEReport savedReport = ameReportService.saveReport(ameReport);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReport);
    }

    @PostMapping("/create")
    @Operation(summary = "Create AME report with parameters", description = "Create a new AME report with specific parameters")
    public ResponseEntity<AMEReport> createReportWithParams(
            @RequestParam Long caseId,
            @RequestParam String doctorName,
            @RequestParam String specialty,
            @RequestParam LocalDate examinationDate,
            @RequestParam String filePath) {

        Optional<WorkersCompCase> caseOpt = workersCompCaseService.getCaseById(caseId);
        if (caseOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AMEReport newReport = ameReportService.createReport(
                caseOpt.get(), doctorName, specialty, examinationDate, filePath);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReport);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update AME report", description = "Update an existing AME report")
    public ResponseEntity<AMEReport> updateReport(@PathVariable Long id,
            @Valid @RequestBody AMEReport ameReport) {
        Optional<AMEReport> existingReport = ameReportService.getReportById(id);
        if (existingReport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ameReport.setId(id);
        AMEReport updatedReport = ameReportService.saveReport(ameReport);
        return ResponseEntity.ok(updatedReport);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete AME report", description = "Delete an AME report")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        Optional<AMEReport> existingReport = ameReportService.getReportById(id);
        if (existingReport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ameReportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    // Search and filtering endpoints
    @GetMapping("/case/{caseId}")
    @Operation(summary = "Get reports by case", description = "Retrieve AME reports for a specific case")
    public ResponseEntity<List<AMEReport>> getReportsByCase(@PathVariable Long caseId) {
        List<AMEReport> reports = ameReportService.getReportsByCaseId(caseId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/search/doctor")
    @Operation(summary = "Search reports by doctor", description = "Search AME reports by doctor name")
    public ResponseEntity<List<AMEReport>> searchByDoctor(@RequestParam String doctorName) {
        List<AMEReport> reports = ameReportService.searchReportsByDoctorName(doctorName);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/specialty/{specialty}")
    @Operation(summary = "Get reports by specialty", description = "Retrieve AME reports by medical specialty")
    public ResponseEntity<List<AMEReport>> getReportsBySpecialty(@PathVariable String specialty) {
        List<AMEReport> reports = ameReportService.getReportsBySpecialty(specialty);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/final/{isFinal}")
    @Operation(summary = "Get reports by final status", description = "Retrieve AME reports by final status")
    public ResponseEntity<List<AMEReport>> getReportsByFinalStatus(@PathVariable Boolean isFinal) {
        List<AMEReport> reports = ameReportService.getFinalReports(isFinal);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get reports by date range", description = "Retrieve AME reports within a date range")
    public ResponseEntity<List<AMEReport>> getReportsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<AMEReport> reports = ameReportService.getReportsByDateRange(startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/case/{caseId}/final")
    @Operation(summary = "Get final reports by case", description = "Retrieve final AME reports for a specific case")
    public ResponseEntity<List<AMEReport>> getFinalReportsByCase(@PathVariable Long caseId) {
        List<AMEReport> reports = ameReportService.getFinalReportsByCase(caseId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/disability-rating/{minRating}")
    @Operation(summary = "Get reports by disability rating", description = "Retrieve AME reports with disability rating >= threshold")
    public ResponseEntity<List<AMEReport>> getReportsByDisabilityRating(@PathVariable BigDecimal minRating) {
        List<AMEReport> reports = ameReportService.getReportsByDisabilityRating(minRating);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/needs-summary")
    @Operation(summary = "Get reports needing summary", description = "Retrieve AME reports that need AI summary generation")
    public ResponseEntity<List<AMEReport>> getReportsNeedingSummary() {
        List<AMEReport> reports = ameReportService.getReportsNeedingSummary();
        return ResponseEntity.ok(reports);
    }

    // AI and business logic endpoints
    @PostMapping("/{id}/generate-summary")
    @Operation(summary = "Generate AI summary", description = "Generate AI summary for an AME report")
    public ResponseEntity<AMEReport> generateAISummary(@PathVariable Long id) {
        try {
            AMEReport updatedReport = ameReportService.generateAISummary(id);
            return ResponseEntity.ok(updatedReport);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/batch-generate-summaries")
    @Operation(summary = "Batch generate AI summaries", description = "Generate AI summaries for all pending reports")
    public ResponseEntity<Integer> batchGenerateSummaries() {
        try {
            int processed = ameReportService.generateAISummariesForPendingReports();
            return ResponseEntity.ok(processed);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get report analytics", description = "Get AME report analytics and statistics")
    public ResponseEntity<AMEReportService.AMEReportAnalytics> getReportAnalytics() {
        AMEReportService.AMEReportAnalytics analytics = ameReportService.getReportAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // Exception handler for this controller
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
    }
}
