package com.legaljava.dto;

import jakarta.validation.constraints.NotBlank;

public class QueryRequest {
    @NotBlank(message = "Query cannot be empty")
    private String query;
    
    private String userId;
    private String sessionId;

    // Constructors
    public QueryRequest() {}
    
    public QueryRequest(String query, String userId, String sessionId) {
        this.query = query;
        this.userId = userId;
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
