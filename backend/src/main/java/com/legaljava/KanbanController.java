package com.legaljava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/columns")
public class KanbanController {

    @Autowired
    private KanbanService kanbanService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getColumns() {
        try {
            // Fetch columns using service
            List<Map<String, Object>> columns = kanbanService.fetchColumns();
            return ResponseEntity.status(HttpStatus.OK).body(columns);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping
    public ResponseEntity<String> updateColumns(@RequestBody Map<String, Object> payload) {
        try {
            // Update columns using service
            kanbanService.updateColumns(payload);
            return ResponseEntity.status(HttpStatus.OK).body("Columns updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating columns: " + e.getMessage());
        }
    }
}
