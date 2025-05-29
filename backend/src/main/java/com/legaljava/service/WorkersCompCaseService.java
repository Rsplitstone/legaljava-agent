package com.legaljava.service;

import com.legaljava.entity.WorkersCompCase;
import com.legaljava.repository.WorkersCompCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class WorkersCompCaseService {

    @Autowired
    private WorkersCompCaseRepository caseRepository;

    public List<WorkersCompCase> getAllCases() {
        return caseRepository.findAll();
    }

    public Optional<WorkersCompCase> getCaseById(Long id) {
        return caseRepository.findById(id);
    }

    public Optional<WorkersCompCase> getCaseByCaseNumber(String caseNumber) {
        return caseRepository.findByCaseNumber(caseNumber);
    }

    public WorkersCompCase saveCase(WorkersCompCase workersCompCase) {
        return caseRepository.save(workersCompCase);
    }

    public WorkersCompCase createCase(String caseNumber, String claimantName, String employerName,
            LocalDate injuryDate, String injuryDescription) {
        WorkersCompCase newCase = new WorkersCompCase(caseNumber, claimantName, employerName,
                injuryDate, injuryDescription);
        return caseRepository.save(newCase);
    }

    public List<WorkersCompCase> searchCasesByClaimant(String claimantName) {
        return caseRepository.findByClaimantNameContainingIgnoreCase(claimantName);
    }

    public List<WorkersCompCase> searchCasesByEmployer(String employerName) {
        return caseRepository.findByEmployerNameContainingIgnoreCase(employerName);
    }

    public List<WorkersCompCase> getCasesByStatus(WorkersCompCase.CaseStatus status) {
        return caseRepository.findByStatus(status);
    }

    public List<WorkersCompCase> getCasesByAdjuster(String adjusterName) {
        return caseRepository.findByAdjusterName(adjusterName);
    }

    public void deleteCase(Long id) {
        caseRepository.deleteById(id);
    }

    // Business logic methods

    /**
     * Calculate days since injury for determining statute of limitations
     */
    public long getDaysSinceInjury(WorkersCompCase workersCompCase) {
        return Period.between(workersCompCase.getInjuryDate(), LocalDate.now()).getDays();
    }

    /**
     * Check if case is approaching statute of limitations (1 year in CA)
     */
    public boolean isApproachingStatuteOfLimitations(WorkersCompCase workersCompCase) {
        long daysSinceInjury = getDaysSinceInjury(workersCompCase);
        return daysSinceInjury > 300; // Alert when within 65 days of 1-year limit
    }

    /**
     * Calculate temporary disability benefits based on weekly wage
     * California rate is 2/3 of average weekly wage, subject to min/max
     */
    public BigDecimal calculateTemporaryDisabilityRate(BigDecimal weeklyWage) {
        if (weeklyWage == null)
            return BigDecimal.ZERO;

        // 2/3 of weekly wage
        BigDecimal rate = weeklyWage.multiply(new BigDecimal("0.6667"));

        // California 2024 TD rates (these would be updated annually)
        BigDecimal maxRate = new BigDecimal("1539.71"); // Max weekly TD rate
        BigDecimal minRate = new BigDecimal("230.95"); // Min weekly TD rate

        if (rate.compareTo(maxRate) > 0)
            return maxRate;
        if (rate.compareTo(minRate) < 0)
            return minRate;

        return rate.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calculate permanent disability indemnity based on disability rating
     */
    public BigDecimal calculatePermanentDisabilityIndemnity(BigDecimal disabilityRating, BigDecimal weeklyWage) {
        if (disabilityRating == null || weeklyWage == null)
            return BigDecimal.ZERO;

        // California PD calculation is complex - this is a simplified version
        // Real calculation involves age factors, disability rating schedules, etc.

        BigDecimal baseRate = weeklyWage.multiply(new BigDecimal("0.6667"));
        BigDecimal maxRate = new BigDecimal("1539.71");
        BigDecimal minRate = new BigDecimal("230.95");

        if (baseRate.compareTo(maxRate) > 0)
            baseRate = maxRate;
        if (baseRate.compareTo(minRate) < 0)
            baseRate = minRate;

        // Multiply by disability rating percentage and number of weeks
        // For example: 10% disability = 30 weeks of benefits
        BigDecimal weeks = disabilityRating.multiply(new BigDecimal("3"));

        return baseRate.multiply(weeks).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Get dashboard statistics
     */
    public WorkersCompDashboard getDashboardStats() {
        long openCases = caseRepository.countByStatus(WorkersCompCase.CaseStatus.OPEN);
        long pendingCases = caseRepository.countByStatus(WorkersCompCase.CaseStatus.PENDING_REVIEW);
        long closedCases = caseRepository.countByStatus(WorkersCompCase.CaseStatus.CLOSED);
        long settledCases = caseRepository.countByStatus(WorkersCompCase.CaseStatus.SETTLED);

        return new WorkersCompDashboard(openCases, pendingCases, closedCases, settledCases);
    }

    public static class WorkersCompDashboard {
        private final long openCases;
        private final long pendingCases;
        private final long closedCases;
        private final long settledCases;

        public WorkersCompDashboard(long openCases, long pendingCases, long closedCases, long settledCases) {
            this.openCases = openCases;
            this.pendingCases = pendingCases;
            this.closedCases = closedCases;
            this.settledCases = settledCases;
        }

        public long getOpenCases() {
            return openCases;
        }

        public long getPendingCases() {
            return pendingCases;
        }

        public long getClosedCases() {
            return closedCases;
        }

        public long getSettledCases() {
            return settledCases;
        }

        public long getTotalCases() {
            return openCases + pendingCases + closedCases + settledCases;
        }
    }
}
