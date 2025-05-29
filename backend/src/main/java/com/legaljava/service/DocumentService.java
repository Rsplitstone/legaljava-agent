package com.legaljava.service;

import com.legaljava.entity.LegalDocument;
import com.legaljava.repository.LegalDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    
    @Autowired
    private LegalDocumentRepository documentRepository;
    
    public List<LegalDocument> getAllDocuments() {
        return documentRepository.findAll();
    }
    
    public Optional<LegalDocument> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }
    
    public List<LegalDocument> searchDocuments(String keyword) {
        return documentRepository.searchByKeyword(keyword);
    }
    
    public List<LegalDocument> getDocumentsByType(String type) {
        return documentRepository.findByDocumentType(type);
    }
    
    public LegalDocument saveDocument(LegalDocument document) {
        return documentRepository.save(document);
    }
    
    public LegalDocument uploadDocument(String title, String documentType, MultipartFile file) throws IOException {
        String content = new String(file.getBytes());
        
        LegalDocument document = new LegalDocument(title, content, documentType);
        return documentRepository.save(document);
    }
    
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
