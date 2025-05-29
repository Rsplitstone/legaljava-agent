package com.legaljava.dto;

import java.util.List;

public class QueryResponse {
    private String response;
    private List<String> citations;
    private String sessionId;
    private Double confidence;

    // Constructors
    public QueryResponse() {}
    
    public QueryResponse(String response, List<String> citations, String sessionId, Double confidence) {
        this.response = response;
        this.citations = citations;
        this.sessionId = sessionId;
        this.confidence = confidence;
    }

    // Getters and Setters
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public List<String> getCitations() { return citations; }
    public void setCitations(List<String> citations) { this.citations = citations; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
