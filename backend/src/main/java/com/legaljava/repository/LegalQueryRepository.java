package com.legaljava.repository;

import com.legaljava.entity.LegalQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalQueryRepository extends JpaRepository<LegalQuery, Long> {
    List<LegalQuery> findByUserId(String userId);
    List<LegalQuery> findBySessionId(String sessionId);
    List<LegalQuery> findByUserIdOrderByCreatedAtDesc(String userId);
}
