package com.legaljava.repository;

import com.legaljava.entity.WorkersCompCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkersCompCaseRepository extends JpaRepository<WorkersCompCase, Long> {

    Optional<WorkersCompCase> findByCaseNumber(String caseNumber);

    List<WorkersCompCase> findByClaimantNameContainingIgnoreCase(String claimantName);

    List<WorkersCompCase> findByEmployerNameContainingIgnoreCase(String employerName);

    List<WorkersCompCase> findByStatus(WorkersCompCase.CaseStatus status);

    List<WorkersCompCase> findByInjuryDateBetween(LocalDate startDate, LocalDate endDate);

    List<WorkersCompCase> findByAdjusterName(String adjusterName);

    @Query("SELECT c FROM WorkersCompCase c WHERE c.disabilityRating >= :minRating")
    List<WorkersCompCase> findByDisabilityRatingGreaterThanEqual(@Param("minRating") java.math.BigDecimal minRating);

    @Query("SELECT c FROM WorkersCompCase c WHERE c.maxMedicalImprovement IS NULL AND c.status = 'OPEN'")
    List<WorkersCompCase> findOpenCasesWithoutMMI();

    @Query("SELECT COUNT(c) FROM WorkersCompCase c WHERE c.status = :status")
    long countByStatus(@Param("status") WorkersCompCase.CaseStatus status);
}
