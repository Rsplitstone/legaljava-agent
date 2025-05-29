package com.legaljava.controller;

import com.legaljava.dto.QueryRequest;
import com.legaljava.dto.QueryResponse;
import com.legaljava.entity.LegalQuery;
import com.legaljava.service.LegalQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
@Tag(name = "Legal Chat", description = "Legal query processing endpoints")
public class ChatController {
    
    @Autowired
    private LegalQueryService legalQueryService;
    
    @PostMapping("/query")
    @Operation(summary = "Process a legal query", description = "Submit a legal question and get an AI-powered response with citations")
    public Mono<ResponseEntity<QueryResponse>> processQuery(@Valid @RequestBody QueryRequest request) {
        return legalQueryService.processQuery(request)
            .map(ResponseEntity::ok)
            .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    @GetMapping("/history/{userId}")
    @Operation(summary = "Get user query history", description = "Retrieve the query history for a specific user")
    public ResponseEntity<List<LegalQuery>> getUserHistory(@PathVariable String userId) {
        List<LegalQuery> history = legalQueryService.getUserQueryHistory(userId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get session queries", description = "Retrieve all queries for a specific session")
    public ResponseEntity<List<LegalQuery>> getSessionQueries(@PathVariable String sessionId) {
        List<LegalQuery> queries = legalQueryService.getSessionQueries(sessionId);
        return ResponseEntity.ok(queries);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the chat service is running")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Legal Java Chat Service is running");
    }
}
