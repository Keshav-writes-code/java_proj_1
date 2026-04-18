package com.enterprise.engine.repository;

import com.enterprise.engine.domain.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditRecord, Long> {
}
