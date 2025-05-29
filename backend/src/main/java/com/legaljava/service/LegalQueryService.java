package com.legaljava.service;

import com.legaljava.dto.QueryRequest;
import com.legaljava.dto.QueryResponse;
import com.legaljava.entity.LegalQuery;
import com.legaljava.repository.LegalQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class LegalQueryService {
    
    @Autowired
    private LegalQueryRepository queryRepository;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Value("${app.py-rag.base-url}")
    private String pyRagBaseUrl;
    
    public Mono<QueryResponse> processQuery(QueryRequest request) {
        // Generate session ID if not provided
        String sessionId = request.getSessionId() != null ? 
            request.getSessionId() : UUID.randomUUID().toString();
        
        // Save the query to database
        LegalQuery legalQuery = new LegalQuery(request.getQuery(), request.getUserId(), sessionId);
        queryRepository.save(legalQuery);
        
        // Call Python RAG service
        return webClientBuilder.build()
            .post()
            .uri(pyRagBaseUrl + "/query")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(QueryResponse.class)
            .doOnNext(response -> {
                // Update the saved query with the response
                legalQuery.setResponse(response.getResponse());
                queryRepository.save(legalQuery);
            })
            .doOnError(error -> {
                // Handle error case
                legalQuery.setResponse("Error processing query: " + error.getMessage());
                queryRepository.save(legalQuery);
            });
    }
    
    public List<LegalQuery> getUserQueryHistory(String userId) {
        return queryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<LegalQuery> getSessionQueries(String sessionId) {
        return queryRepository.findBySessionId(sessionId);
    }
}
