package com.legaljava.controller;

import com.legaljava.entity.CaseTask;
import com.legaljava.entity.WorkersCompCase;
import com.legaljava.service.CaseTaskService;
import com.legaljava.service.WorkersCompCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/case-tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Case Tasks", description = "Case task management endpoints")
public class CaseTaskController {

    @Autowired
    private CaseTaskService caseTaskService;

    @Autowired
    private WorkersCompCaseService workersCompCaseService;

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve all case tasks")
    public ResponseEntity<List<CaseTask>> getAllTasks() {
        List<CaseTask> tasks = caseTaskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve a specific case task by ID")
    public ResponseEntity<CaseTask> getTaskById(@PathVariable Long id) {
        Optional<CaseTask> taskOpt = caseTaskService.getTaskById(id);
        return taskOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new task", description = "Create a new case task")
    public ResponseEntity<CaseTask> createTask(@Valid @RequestBody CaseTask caseTask) {
        CaseTask savedTask = caseTaskService.saveTask(caseTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @PostMapping("/create")
    @Operation(summary = "Create task with parameters", description = "Create a new task with specific parameters")
    public ResponseEntity<CaseTask> createTaskWithParams(
            @RequestParam Long caseId,
            @RequestParam String title,
            @RequestParam CaseTask.TaskType taskType,
            @RequestParam LocalDate dueDate,
            @RequestParam CaseTask.TaskPriority priority) {

        Optional<WorkersCompCase> caseOpt = workersCompCaseService.getCaseById(caseId);
        if (caseOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CaseTask newTask = caseTaskService.createTask(
                caseOpt.get(), title, taskType, dueDate, priority);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @PostMapping("/create-detailed")
    @Operation(summary = "Create detailed task", description = "Create a new task with full details")
    public ResponseEntity<CaseTask> createDetailedTask(
            @RequestParam Long caseId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam CaseTask.TaskType taskType,
            @RequestParam LocalDate dueDate,
            @RequestParam CaseTask.TaskPriority priority,
            @RequestParam(required = false) String assignedTo) {

        Optional<WorkersCompCase> caseOpt = workersCompCaseService.getCaseById(caseId);
        if (caseOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CaseTask newTask = caseTaskService.createTaskWithDetails(
                caseOpt.get(), title, description, taskType, dueDate, priority, assignedTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Update an existing case task")
    public ResponseEntity<CaseTask> updateTask(@PathVariable Long id,
            @Valid @RequestBody CaseTask caseTask) {
        Optional<CaseTask> existingTask = caseTaskService.getTaskById(id);
        if (existingTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        caseTask.setId(id);
        CaseTask updatedTask = caseTaskService.saveTask(caseTask);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Delete a case task")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Optional<CaseTask> existingTask = caseTaskService.getTaskById(id);
        if (existingTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        caseTaskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // Search and filtering endpoints
    @GetMapping("/case/{caseId}")
    @Operation(summary = "Get tasks by case", description = "Retrieve tasks for a specific case")
    public ResponseEntity<List<CaseTask>> getTasksByCase(@PathVariable Long caseId) {
        List<CaseTask> tasks = caseTaskService.getTasksByCaseId(caseId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieve tasks by status")
    public ResponseEntity<List<CaseTask>> getTasksByStatus(@PathVariable CaseTask.TaskStatus status) {
        List<CaseTask> tasks = caseTaskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/type/{taskType}")
    @Operation(summary = "Get tasks by type", description = "Retrieve tasks by task type")
    public ResponseEntity<List<CaseTask>> getTasksByType(@PathVariable CaseTask.TaskType taskType) {
        List<CaseTask> tasks = caseTaskService.getTasksByType(taskType);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get tasks by priority", description = "Retrieve tasks by priority")
    public ResponseEntity<List<CaseTask>> getTasksByPriority(@PathVariable CaseTask.TaskPriority priority) {
        List<CaseTask> tasks = caseTaskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assignee/{assignedTo}")
    @Operation(summary = "Get tasks by assignee", description = "Retrieve tasks assigned to a specific user")
    public ResponseEntity<List<CaseTask>> getTasksByAssignee(@PathVariable String assignedTo) {
        List<CaseTask> tasks = caseTaskService.getTasksByAssignee(assignedTo);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assignee/{assignedTo}/pending")
    @Operation(summary = "Get pending tasks by assignee", description = "Retrieve pending tasks for a user ordered by priority and due date")
    public ResponseEntity<List<CaseTask>> getPendingTasksByAssignee(@PathVariable String assignedTo) {
        List<CaseTask> tasks = caseTaskService.getPendingTasksByAssignee(assignedTo);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get tasks by date range", description = "Retrieve tasks due within a date range")
    public ResponseEntity<List<CaseTask>> getTasksByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<CaseTask> tasks = caseTaskService.getTasksByDateRange(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks", description = "Retrieve all overdue tasks")
    public ResponseEntity<List<CaseTask>> getOverdueTasks() {
        List<CaseTask> tasks = caseTaskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-today")
    @Operation(summary = "Get tasks due today", description = "Retrieve tasks due today")
    public ResponseEntity<List<CaseTask>> getTasksDueToday() {
        List<CaseTask> tasks = caseTaskService.getTasksDueToday();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-this-week")
    @Operation(summary = "Get tasks due this week", description = "Retrieve tasks due this week")
    public ResponseEntity<List<CaseTask>> getTasksDueThisWeek() {
        List<CaseTask> tasks = caseTaskService.getTasksDueThisWeek();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-by/{date}")
    @Operation(summary = "Get tasks due by date", description = "Retrieve tasks due by a specific date")
    public ResponseEntity<List<CaseTask>> getTasksDueByDate(@PathVariable LocalDate date) {
        List<CaseTask> tasks = caseTaskService.getTasksDueByDate(date);
        return ResponseEntity.ok(tasks);
    }

    // Task management endpoints
    @PutMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Update the status of a task")
    public ResponseEntity<CaseTask> updateTaskStatus(@PathVariable Long id,
            @RequestParam CaseTask.TaskStatus status) {
        try {
            CaseTask updatedTask = caseTaskService.updateTaskStatus(id, status);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Complete task", description = "Mark a task as completed with optional notes")
    public ResponseEntity<CaseTask> completeTask(@PathVariable Long id,
            @RequestParam(required = false) String notes) {
        try {
            CaseTask completedTask = caseTaskService.completeTask(id, notes);
            return ResponseEntity.ok(completedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign task", description = "Assign a task to a user")
    public ResponseEntity<CaseTask> assignTask(@PathVariable Long id,
            @RequestParam String assignedTo) {
        try {
            CaseTask assignedTask = caseTaskService.assignTask(id, assignedTo);
            return ResponseEntity.ok(assignedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/priority")
    @Operation(summary = "Update task priority", description = "Update the priority of a task")
    public ResponseEntity<CaseTask> updateTaskPriority(@PathVariable Long id,
            @RequestParam CaseTask.TaskPriority priority) {
        try {
            CaseTask updatedTask = caseTaskService.updateTaskPriority(id, priority);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/due-date")
    @Operation(summary = "Update task due date", description = "Update the due date of a task")
    public ResponseEntity<CaseTask> updateTaskDueDate(@PathVariable Long id,
            @RequestParam LocalDate newDueDate) {
        try {
            CaseTask updatedTask = caseTaskService.updateTaskDueDate(id, newDueDate);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/add-notes")
    @Operation(summary = "Add task notes", description = "Add notes to a task")
    public ResponseEntity<CaseTask> addTaskNotes(@PathVariable Long id,
            @RequestParam String notes) {
        try {
            CaseTask updatedTask = caseTaskService.addTaskNotes(id, notes);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Bulk operations
    @PostMapping("/case/{caseId}/create-standard")
    @Operation(summary = "Create standard tasks", description = "Create standard tasks for a new case")
    public ResponseEntity<List<CaseTask>> createStandardTasks(@PathVariable Long caseId) {
        Optional<WorkersCompCase> caseOpt = workersCompCaseService.getCaseById(caseId);
        if (caseOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<CaseTask> standardTasks = caseTaskService.createStandardTasksForCase(caseOpt.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(standardTasks);
    }

    @PostMapping("/mark-overdue")
    @Operation(summary = "Mark overdue tasks", description = "Mark all overdue tasks with OVERDUE status")
    public ResponseEntity<Integer> markOverdueTasks() {
        int markedCount = caseTaskService.markOverdueTasks();
        return ResponseEntity.ok(markedCount);
    }

    // Analytics endpoints
    @GetMapping("/analytics")
    @Operation(summary = "Get task analytics", description = "Get task analytics and statistics")
    public ResponseEntity<CaseTaskService.TaskAnalytics> getTaskAnalytics() {
        CaseTaskService.TaskAnalytics analytics = caseTaskService.getTaskAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/analytics/user/{assignedTo}")
    @Operation(summary = "Get user task analytics", description = "Get task analytics for a specific user")
    public ResponseEntity<CaseTaskService.TaskAnalytics> getUserTaskAnalytics(@PathVariable String assignedTo) {
        CaseTaskService.TaskAnalytics analytics = caseTaskService.getUserTaskAnalytics(assignedTo);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/case/{caseId}/count/{status}")
    @Operation(summary = "Count tasks by case and status", description = "Count tasks for a case by status")
    public ResponseEntity<Long> countTasksByCaseAndStatus(@PathVariable Long caseId,
            @PathVariable CaseTask.TaskStatus status) {
        long count = caseTaskService.countTasksByCaseAndStatus(caseId, status);
        return ResponseEntity.ok(count);
    }

    // Exception handler for this controller
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
    }
}
