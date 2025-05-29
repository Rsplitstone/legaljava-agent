package com.legaljava.controller;

import com.legaljava.entity.LegalDocument;
import com.legaljava.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "*")
@Tag(name = "Document Management", description = "Legal document management endpoints")
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @GetMapping
    @Operation(summary = "Get all documents", description = "Retrieve all legal documents")
    public ResponseEntity<List<LegalDocument>> getAllDocuments() {
        List<LegalDocument> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Retrieve a specific legal document by ID")
    public ResponseEntity<LegalDocument> getDocumentById(@PathVariable Long id) {
        Optional<LegalDocument> document = documentService.getDocumentById(id);
        return document.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search documents", description = "Search legal documents by keyword")
    public ResponseEntity<List<LegalDocument>> searchDocuments(@RequestParam String keyword) {
        List<LegalDocument> documents = documentService.searchDocuments(keyword);
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Get documents by type", description = "Retrieve documents by document type")
    public ResponseEntity<List<LegalDocument>> getDocumentsByType(@PathVariable String type) {
        List<LegalDocument> documents = documentService.getDocumentsByType(type);
        return ResponseEntity.ok(documents);
    }
    
    @PostMapping("/upload")
    @Operation(summary = "Upload document", description = "Upload a new legal document")
    public ResponseEntity<LegalDocument> uploadDocument(
            @RequestParam("title") String title,
            @RequestParam("type") String documentType,
            @RequestParam("file") MultipartFile file) {
        try {
            LegalDocument document = documentService.uploadDocument(title, documentType, file);
            return ResponseEntity.ok(document);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Create document", description = "Create a new legal document")
    public ResponseEntity<LegalDocument> createDocument(@RequestBody LegalDocument document) {
        LegalDocument savedDocument = documentService.saveDocument(document);
        return ResponseEntity.ok(savedDocument);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Delete a legal document by ID")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
