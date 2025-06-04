package com.legaljava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Kanban Board", description = "Endpoints for managing the kanban board")
public class KanbanController {

    @Operation(summary = "Get kanban columns", description = "Returns all columns for the kanban board")
    @GetMapping("/columns")
    public ResponseEntity<String> getColumns() {
        try {
            // Load the mock data from the static JSON file
            Resource resource = new ClassPathResource("public/mock/columns.json");
            String content = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error loading columns data");
        }
    }

    @Operation(summary = "Update kanban columns", description = "Updates the columns for the kanban board")
    @PatchMapping("/columns")
    public ResponseEntity<String> updateColumns(@RequestBody String columns) {
        // In a real implementation, this would save the data
        // For now, we just return the data that was sent
        return ResponseEntity.ok(columns);
    }
}
