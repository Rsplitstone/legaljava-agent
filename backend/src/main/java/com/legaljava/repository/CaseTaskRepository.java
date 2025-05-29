package com.legaljava.repository;

import com.legaljava.entity.CaseTask;
import com.legaljava.entity.WorkersCompCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CaseTaskRepository extends JpaRepository<CaseTask, Long> {

    List<CaseTask> findByWorkersCompCase(WorkersCompCase workersCompCase);

    List<CaseTask> findByWorkersCompCaseId(Long caseId);

    List<CaseTask> findByStatus(CaseTask.TaskStatus status);

    List<CaseTask> findByTaskType(CaseTask.TaskType taskType);

    List<CaseTask> findByPriority(CaseTask.TaskPriority priority);

    List<CaseTask> findByAssignedTo(String assignedTo);

    List<CaseTask> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM CaseTask t WHERE t.dueDate < :currentDate AND t.status != 'COMPLETED' AND t.status != 'CANCELLED'")
    List<CaseTask> findOverdueTasks(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT t FROM CaseTask t WHERE t.dueDate <= :date AND t.status = 'PENDING'")
    List<CaseTask> findTasksDueByDate(@Param("date") LocalDate date);

    @Query("SELECT t FROM CaseTask t WHERE t.assignedTo = :assignedTo AND t.status = 'PENDING' ORDER BY t.priority DESC, t.dueDate ASC")
    List<CaseTask> findPendingTasksByAssigneeOrderedByPriorityAndDueDate(@Param("assignedTo") String assignedTo);

    @Query("SELECT COUNT(t) FROM CaseTask t WHERE t.workersCompCase.id = :caseId AND t.status = :status")
    long countByCaseIdAndStatus(@Param("caseId") Long caseId, @Param("status") CaseTask.TaskStatus status);
}
