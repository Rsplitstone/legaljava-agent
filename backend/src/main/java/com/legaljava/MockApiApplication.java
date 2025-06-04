package com.legaljava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class MockApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MockApiApplication.class, args);
    }    @RestController
    @RequestMapping("/")
    @CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
    public static class MockApiController {

        @GetMapping("/health")
        public ResponseEntity<Map<String, String>> health() {
            Map<String, String> response = new HashMap<>();
            response.put("status", "UP");
            response.put("message", "Mock API is running");
            return ResponseEntity.ok(response);
        }

        @GetMapping("/stats")
        public ResponseEntity<Map<String, Object>> stats() {
            Map<String, Object> response = new HashMap<>();
            response.put("totalCases", 42);
            response.put("activeCases", 24);
            response.put("documentsProcessed", 156);
            response.put("aiQueriesHandled", 87);
            return ResponseEntity.ok(response);
        }

        @GetMapping("/cases/recent")
        public ResponseEntity<List<Map<String, Object>>> recentCases() {
            List<Map<String, Object>> cases = Arrays.asList(
                    createCase(1, "Johnson v. ABC Corp", "Workers Comp", "Active", "2025-05-15"),
                    createCase(2, "Smith v. XYZ Inc", "Workers Comp", "Under Review", "2025-05-20"),
                    createCase(3, "Davis v. 123 Industries", "Workers Comp", "Pending", "2025-05-25"));
            return ResponseEntity.ok(cases);
        }

        private Map<String, Object> createCase(int id, String title, String type, String status, String date) {
            Map<String, Object> caseMap = new HashMap<>();
            caseMap.put("id", id);
            caseMap.put("title", title);
            caseMap.put("type", type);
            caseMap.put("status", status);
            caseMap.put("date", date);
            return caseMap;
        }
    }
}
