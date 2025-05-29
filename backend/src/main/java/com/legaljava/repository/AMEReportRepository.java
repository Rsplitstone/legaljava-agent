package com.legaljava.repository;

import com.legaljava.entity.AMEReport;
import com.legaljava.entity.WorkersCompCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AMEReportRepository extends JpaRepository<AMEReport, Long> {

    List<AMEReport> findByWorkersCompCase(WorkersCompCase workersCompCase);

    List<AMEReport> findByWorkersCompCaseId(Long caseId);

    List<AMEReport> findByDoctorNameContainingIgnoreCase(String doctorName);

    List<AMEReport> findBySpecialty(String specialty);

    List<AMEReport> findByIsFinal(Boolean isFinal);

    List<AMEReport> findByExaminationDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM AMEReport r WHERE r.workersCompCase.id = :caseId AND r.isFinal = true")
    List<AMEReport> findFinalReportsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT r FROM AMEReport r WHERE r.aiSummary IS NULL")
    List<AMEReport> findReportsNeedingSummary();

    @Query("SELECT r FROM AMEReport r WHERE r.recommendedDisabilityRating >= :minRating")
    List<AMEReport> findByDisabilityRatingGreaterThanEqual(@Param("minRating") java.math.BigDecimal minRating);
}
