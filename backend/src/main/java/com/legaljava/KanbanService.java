package com.legaljava;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KanbanService {

    public List<Map<String, Object>> fetchColumns() {
        // Mock data for now - replace with actual database logic
        return List.of(
            Map.of("id", "1", "title", "To Do", "tasks", List.of(Map.of("id", "101", "name", "Task 1"))),
            Map.of("id", "2", "title", "In Progress", "tasks", List.of(Map.of("id", "102", "name", "Task 2"))),
            Map.of("id", "3", "title", "Done", "tasks", List.of(Map.of("id", "103", "name", "Task 3")))
        );
    }

    public void updateColumns(Map<String, Object> payload) {
        // Mock update logic - replace with actual database logic
        System.out.println("Updating columns with payload: " + payload);
    }
}
