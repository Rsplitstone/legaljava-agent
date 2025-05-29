package com.legaljava.repository;

import com.legaljava.entity.LegalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalDocumentRepository extends JpaRepository<LegalDocument, Long> {
    
    List<LegalDocument> findByDocumentType(String documentType);
    
    @Query("SELECT d FROM LegalDocument d WHERE d.title ILIKE %:keyword% OR d.content ILIKE %:keyword%")
    List<LegalDocument> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT d FROM LegalDocument d WHERE d.documentType = :type AND (d.title ILIKE %:keyword% OR d.content ILIKE %:keyword%)")
    List<LegalDocument> searchByTypeAndKeyword(@Param("type") String type, @Param("keyword") String keyword);
}
