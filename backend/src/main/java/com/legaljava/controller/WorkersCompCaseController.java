package com.legaljava.controller;

import com.legaljava.entity.WorkersCompCase;
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
@RequestMapping("/api/workers-comp-cases")
@CrossOrigin(origins = "*")
@Tag(name = "Workers' Compensation Cases", description = "Workers' compensation case management endpoints")
public class WorkersCompCaseController {

    @Autowired
    private WorkersCompCaseService workersCompCaseService;

    @GetMapping
    @Operation(summary = "Get all cases", description = "Retrieve all workers' compensation cases")
    public ResponseEntity<List<WorkersCompCase>> getAllCases() {
        List<WorkersCompCase> cases = workersCompCaseService.getAllCases();
        return ResponseEntity.ok(cases);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get case by ID", description = "Retrieve a specific workers' compensation case by ID")
    public ResponseEntity<WorkersCompCase> getCaseById(@PathVariable Long id) {
        Optional<WorkersCompCase> caseOpt = workersCompCaseService.getCaseById(id);
        return caseOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/case-number/{caseNumber}")
    @Operation(summary = "Get case by case number", description = "Retrieve a case by its case number")
    public ResponseEntity<WorkersCompCase> getCaseByCaseNumber(@PathVariable String caseNumber) {
        Optional<WorkersCompCase> caseOpt = workersCompCaseService.getCaseByCaseNumber(caseNumber);
        return caseOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new case", description = "Create a new workers' compensation case")
    public ResponseEntity<WorkersCompCase> createCase(@Valid @RequestBody WorkersCompCase workersCompCase) {
        WorkersCompCase savedCase = workersCompCaseService.saveCase(workersCompCase);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCase);
    }

    @PostMapping("/create")
    @Operation(summary = "Create case with parameters", description = "Create a new case with specific parameters")
    public ResponseEntity<WorkersCompCase> createCaseWithParams(
            @RequestParam String caseNumber,
            @RequestParam String claimantName,
            @RequestParam String employerName,
            @RequestParam LocalDate injuryDate,
            @RequestParam String injuryDescription) {

        WorkersCompCase newCase = workersCompCaseService.createCase(
                caseNumber, claimantName, employerName, injuryDate, injuryDescription);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCase);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update case", description = "Update an existing workers' compensation case")
    public ResponseEntity<WorkersCompCase> updateCase(@PathVariable Long id,
            @Valid @RequestBody WorkersCompCase workersCompCase) {
        Optional<WorkersCompCase> existingCase = workersCompCaseService.getCaseById(id);
        if (existingCase.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        workersCompCase.setId(id);
        WorkersCompCase updatedCase = workersCompCaseService.saveCase(workersCompCase);
        return ResponseEntity.ok(updatedCase);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete case", description = "Delete a workers' compensation case")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        Optional<WorkersCompCase> existingCase = workersCompCaseService.getCaseById(id);
        if (existingCase.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        workersCompCaseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }

    // Search and filtering endpoints
    @GetMapping("/search/claimant")
    @Operation(summary = "Search cases by claimant", description = "Search cases by claimant name")
    public ResponseEntity<List<WorkersCompCase>> searchByClaimant(@RequestParam String claimantName) {
        List<WorkersCompCase> cases = workersCompCaseService.searchCasesByClaimant(claimantName);
        return ResponseEntity.ok(cases);
    }

    @GetMapping("/search/employer")
    @Operation(summary = "Search cases by employer", description = "Search cases by employer name")
    public ResponseEntity<List<WorkersCompCase>> searchByEmployer(@RequestParam String employerName) {
        List<WorkersCompCase> cases = workersCompCaseService.searchCasesByEmployer(employerName);
        return ResponseEntity.ok(cases);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get cases by status", description = "Retrieve cases by status")
    public ResponseEntity<List<WorkersCompCase>> getCasesByStatus(@PathVariable WorkersCompCase.CaseStatus status) {
        List<WorkersCompCase> cases = workersCompCaseService.getCasesByStatus(status);
        return ResponseEntity.ok(cases);
    }

    @GetMapping("/adjuster/{adjusterName}")
    @Operation(summary = "Get cases by adjuster", description = "Retrieve cases assigned to a specific adjuster")
    public ResponseEntity<List<WorkersCompCase>> getCasesByAdjuster(@PathVariable String adjusterName) {
        List<WorkersCompCase> cases = workersCompCaseService.getCasesByAdjuster(adjusterName);
        return ResponseEntity.ok(cases);
    }

    // Business logic endpoints
    @GetMapping("/{id}/statute-check")
    @Operation(summary = "Check statute of limitations", description = "Check if case is approaching statute of limitations")
    public ResponseEntity<Boolean> checkStatuteOfLimitations(@PathVariable Long id) {
        Optional<WorkersCompCase> caseOpt = workersCompCaseService.getCaseById(id);
        if (caseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean approaching = workersCompCaseService.isApproachingStatuteOfLimitations(caseOpt.get());
        return ResponseEntity.ok(approaching);
    }

    @GetMapping("/{id}/days-since-injury")
    @Operation(summary = "Get days since injury", description = "Calculate days since injury date")
    public ResponseEntity<Long> getDaysSinceInjury(@PathVariable Long id) {
        Optional<WorkersCompCase> caseOpt = workersCompCaseService.getCaseById(id);
        if (caseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        long days = workersCompCaseService.getDaysSinceInjury(caseOpt.get());
        return ResponseEntity.ok(days);
    }

    @PostMapping("/calculate-td-rate")
    @Operation(summary = "Calculate temporary disability rate", description = "Calculate temporary disability rate based on weekly wage")
    public ResponseEntity<BigDecimal> calculateTDRate(@RequestParam BigDecimal weeklyWage) {
        BigDecimal rate = workersCompCaseService.calculateTemporaryDisabilityRate(weeklyWage);
        return ResponseEntity.ok(rate);
    }

    @PostMapping("/calculate-pd-indemnity")
    @Operation(summary = "Calculate permanent disability indemnity", description = "Calculate permanent disability indemnity")
    public ResponseEntity<BigDecimal> calculatePDIndemnity(
            @RequestParam BigDecimal disabilityRating,
            @RequestParam BigDecimal weeklyWage) {
        BigDecimal indemnity = workersCompCaseService.calculatePermanentDisabilityIndemnity(disabilityRating,
                weeklyWage);
        return ResponseEntity.ok(indemnity);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Get workers' compensation dashboard statistics")
    public ResponseEntity<WorkersCompCaseService.WorkersCompDashboard> getDashboardStats() {
        WorkersCompCaseService.WorkersCompDashboard dashboard = workersCompCaseService.getDashboardStats();
        return ResponseEntity.ok(dashboard);
    }

    // Exception handler for this controller
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
    }
}
