package com.legaljava.service;

import com.legaljava.entity.CaseTask;
import com.legaljava.entity.WorkersCompCase;
import com.legaljava.repository.CaseTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CaseTaskService {

    @Autowired
    private CaseTaskRepository caseTaskRepository;

    // Basic CRUD operations
    public List<CaseTask> getAllTasks() {
        return caseTaskRepository.findAll();
    }

    public Optional<CaseTask> getTaskById(Long id) {
        return caseTaskRepository.findById(id);
    }

    public CaseTask saveTask(CaseTask caseTask) {
        return caseTaskRepository.save(caseTask);
    }

    public void deleteTask(Long id) {
        caseTaskRepository.deleteById(id);
    }

    // Business logic methods
    public List<CaseTask> getTasksByCase(WorkersCompCase workersCompCase) {
        return caseTaskRepository.findByWorkersCompCase(workersCompCase);
    }

    public List<CaseTask> getTasksByCaseId(Long caseId) {
        return caseTaskRepository.findByWorkersCompCaseId(caseId);
    }

    public List<CaseTask> getTasksByStatus(CaseTask.TaskStatus status) {
        return caseTaskRepository.findByStatus(status);
    }

    public List<CaseTask> getTasksByType(CaseTask.TaskType taskType) {
        return caseTaskRepository.findByTaskType(taskType);
    }

    public List<CaseTask> getTasksByPriority(CaseTask.TaskPriority priority) {
        return caseTaskRepository.findByPriority(priority);
    }

    public List<CaseTask> getTasksByAssignee(String assignedTo) {
        return caseTaskRepository.findByAssignedTo(assignedTo);
    }

    public List<CaseTask> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        return caseTaskRepository.findByDueDateBetween(startDate, endDate);
    }

    public List<CaseTask> getOverdueTasks() {
        return caseTaskRepository.findOverdueTasks(LocalDate.now());
    }

    public List<CaseTask> getTasksDueByDate(LocalDate date) {
        return caseTaskRepository.findTasksDueByDate(date);
    }

    public List<CaseTask> getPendingTasksByAssignee(String assignedTo) {
        return caseTaskRepository.findPendingTasksByAssigneeOrderedByPriorityAndDueDate(assignedTo);
    }

    public long countTasksByCaseAndStatus(Long caseId, CaseTask.TaskStatus status) {
        return caseTaskRepository.countByCaseIdAndStatus(caseId, status);
    }

    /**
     * Create a new task
     */
    public CaseTask createTask(WorkersCompCase workersCompCase, String title,
            CaseTask.TaskType taskType, LocalDate dueDate,
            CaseTask.TaskPriority priority) {
        CaseTask task = new CaseTask(workersCompCase, title, taskType, dueDate, priority);
        return caseTaskRepository.save(task);
    }

    /**
     * Create a task with full details
     */
    public CaseTask createTaskWithDetails(WorkersCompCase workersCompCase, String title,
            String description, CaseTask.TaskType taskType,
            LocalDate dueDate, CaseTask.TaskPriority priority,
            String assignedTo) {
        CaseTask task = new CaseTask(workersCompCase, title, taskType, dueDate, priority);
        task.setDescription(description);
        task.setAssignedTo(assignedTo);
        return caseTaskRepository.save(task);
    }

    /**
     * Update task status
     */
    public CaseTask updateTaskStatus(Long taskId, CaseTask.TaskStatus status) {
        Optional<CaseTask> taskOpt = caseTaskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }

        CaseTask task = taskOpt.get();
        task.setStatus(status);

        if (status == CaseTask.TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        return caseTaskRepository.save(task);
    }

    /**
     * Complete a task
     */
    public CaseTask completeTask(Long taskId, String notes) {
        Optional<CaseTask> taskOpt = caseTaskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }

        CaseTask task = taskOpt.get();
        task.setStatus(CaseTask.TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());

        if (notes != null && !notes.trim().isEmpty()) {
            String existingNotes = task.getNotes();
            String updatedNotes = existingNotes != null ? existingNotes + "\n\nCompleted: " + notes
                    : "Completed: " + notes;
            task.setNotes(updatedNotes);
        }

        return caseTaskRepository.save(task);
    }

    /**
     * Assign task to a user
     */
    public CaseTask assignTask(Long taskId, String assignedTo) {
        Optional<CaseTask> taskOpt = caseTaskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }

        CaseTask task = taskOpt.get();
        task.setAssignedTo(assignedTo);
        return caseTaskRepository.save(task);
    }

    /**
     * Update task priority
     */
    public CaseTask updateTaskPriority(Long taskId, CaseTask.TaskPriority priority) {
        Optional<CaseTask> taskOpt = caseTaskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }

        CaseTask task = taskOpt.get();
        task.setPriority(priority);
        return caseTaskRepository.save(task);
    }

    /**
     * Update task due date
     */
    public CaseTask updateTaskDueDate(Long taskId, LocalDate newDueDate) {
        Optional<CaseTask> taskOpt = caseTaskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }

        CaseTask task = taskOpt.get();
        task.setDueDate(newDueDate);
        return caseTaskRepository.save(task);
    }

    /**
     * Add notes to a task
     */
    public CaseTask addTaskNotes(Long taskId, String notes) {
        Optional<CaseTask> taskOpt = caseTaskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }

        CaseTask task = taskOpt.get();
        String existingNotes = task.getNotes();
        String updatedNotes = existingNotes != null ? existingNotes + "\n\n" + LocalDateTime.now() + ": " + notes
                : LocalDateTime.now() + ": " + notes;
        task.setNotes(updatedNotes);

        return caseTaskRepository.save(task);
    }

    /**
     * Get tasks due today
     */
    public List<CaseTask> getTasksDueToday() {
        return getTasksDueByDate(LocalDate.now());
    }

    /**
     * Get tasks due this week
     */
    public List<CaseTask> getTasksDueThisWeek() {
        LocalDate startOfWeek = LocalDate.now();
        LocalDate endOfWeek = startOfWeek.plusDays(7);
        return getTasksByDateRange(startOfWeek, endOfWeek);
    }

    /**
     * Auto-create standard tasks for a new workers' comp case
     */
    public List<CaseTask> createStandardTasksForCase(WorkersCompCase workersCompCase) {
        List<CaseTask> standardTasks = List.of(
                new CaseTask(workersCompCase, "Initial Case Review",
                        CaseTask.TaskType.DOCUMENT_REVIEW,
                        LocalDate.now().plusDays(1), CaseTask.TaskPriority.HIGH),

                new CaseTask(workersCompCase, "Medical Records Review",
                        CaseTask.TaskType.MEDICAL_REVIEW,
                        LocalDate.now().plusDays(3), CaseTask.TaskPriority.MEDIUM),

                new CaseTask(workersCompCase, "Benefit Calculation",
                        CaseTask.TaskType.BENEFIT_CALCULATION,
                        LocalDate.now().plusDays(5), CaseTask.TaskPriority.MEDIUM),

                new CaseTask(workersCompCase, "Initial Correspondence",
                        CaseTask.TaskType.CORRESPONDENCE,
                        LocalDate.now().plusDays(2), CaseTask.TaskPriority.MEDIUM),

                new CaseTask(workersCompCase, "Statute of Limitations Check",
                        CaseTask.TaskType.DEADLINE_COMPLIANCE,
                        LocalDate.now().plusDays(30), CaseTask.TaskPriority.HIGH));

        return caseTaskRepository.saveAll(standardTasks);
    }

    /**
     * Mark overdue tasks
     */
    public int markOverdueTasks() {
        List<CaseTask> overdueTasks = getOverdueTasks();
        int markedCount = 0;

        for (CaseTask task : overdueTasks) {
            if (task.getStatus() == CaseTask.TaskStatus.PENDING ||
                    task.getStatus() == CaseTask.TaskStatus.IN_PROGRESS) {
                task.setStatus(CaseTask.TaskStatus.OVERDUE);
                caseTaskRepository.save(task);
                markedCount++;
            }
        }

        return markedCount;
    }

    /**
     * Get task analytics/dashboard data
     */
    public TaskAnalytics getTaskAnalytics() {
        long totalTasks = caseTaskRepository.count();
        long pendingTasks = caseTaskRepository.findByStatus(CaseTask.TaskStatus.PENDING).size();
        long inProgressTasks = caseTaskRepository.findByStatus(CaseTask.TaskStatus.IN_PROGRESS).size();
        long completedTasks = caseTaskRepository.findByStatus(CaseTask.TaskStatus.COMPLETED).size();
        long overdueTasks = getOverdueTasks().size();
        long tasksDueToday = getTasksDueToday().size();
        long tasksDueThisWeek = getTasksDueThisWeek().size();

        return new TaskAnalytics(totalTasks, pendingTasks, inProgressTasks,
                completedTasks, overdueTasks, tasksDueToday, tasksDueThisWeek);
    }

    /**
     * Get user-specific task analytics
     */
    public TaskAnalytics getUserTaskAnalytics(String assignedTo) {
        List<CaseTask> userTasks = getTasksByAssignee(assignedTo);

        long totalTasks = userTasks.size();
        long pendingTasks = userTasks.stream().mapToInt(t -> t.getStatus() == CaseTask.TaskStatus.PENDING ? 1 : 0)
                .sum();
        long inProgressTasks = userTasks.stream()
                .mapToInt(t -> t.getStatus() == CaseTask.TaskStatus.IN_PROGRESS ? 1 : 0).sum();
        long completedTasks = userTasks.stream().mapToInt(t -> t.getStatus() == CaseTask.TaskStatus.COMPLETED ? 1 : 0)
                .sum();

        long overdueTasks = userTasks.stream()
                .mapToInt(t -> (t.getDueDate().isBefore(LocalDate.now()) &&
                        t.getStatus() != CaseTask.TaskStatus.COMPLETED &&
                        t.getStatus() != CaseTask.TaskStatus.CANCELLED) ? 1 : 0)
                .sum();

        long tasksDueToday = userTasks.stream()
                .mapToInt(t -> t.getDueDate().equals(LocalDate.now()) &&
                        t.getStatus() == CaseTask.TaskStatus.PENDING ? 1 : 0)
                .sum();

        long tasksDueThisWeek = userTasks.stream()
                .mapToInt(t -> t.getDueDate().isAfter(LocalDate.now()) &&
                        t.getDueDate().isBefore(LocalDate.now().plusDays(8)) &&
                        t.getStatus() == CaseTask.TaskStatus.PENDING ? 1 : 0)
                .sum();

        return new TaskAnalytics(totalTasks, pendingTasks, inProgressTasks,
                completedTasks, overdueTasks, tasksDueToday, tasksDueThisWeek);
    }

    // Analytics DTO
    public static class TaskAnalytics {
        private final long totalTasks;
        private final long pendingTasks;
        private final long inProgressTasks;
        private final long completedTasks;
        private final long overdueTasks;
        private final long tasksDueToday;
        private final long tasksDueThisWeek;

        public TaskAnalytics(long totalTasks, long pendingTasks, long inProgressTasks,
                long completedTasks, long overdueTasks, long tasksDueToday, long tasksDueThisWeek) {
            this.totalTasks = totalTasks;
            this.pendingTasks = pendingTasks;
            this.inProgressTasks = inProgressTasks;
            this.completedTasks = completedTasks;
            this.overdueTasks = overdueTasks;
            this.tasksDueToday = tasksDueToday;
            this.tasksDueThisWeek = tasksDueThisWeek;
        }

        public long getTotalTasks() {
            return totalTasks;
        }

        public long getPendingTasks() {
            return pendingTasks;
        }

        public long getInProgressTasks() {
            return inProgressTasks;
        }

        public long getCompletedTasks() {
            return completedTasks;
        }

        public long getOverdueTasks() {
            return overdueTasks;
        }

        public long getTasksDueToday() {
            return tasksDueToday;
        }

        public long getTasksDueThisWeek() {
            return tasksDueThisWeek;
        }
    }
}
