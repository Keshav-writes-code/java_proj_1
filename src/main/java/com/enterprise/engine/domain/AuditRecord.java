package com.enterprise.engine.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_records")
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String methodName;

    private String parametersInfo; // Securely stringified params

    private long executionTimeMs;

    private LocalDateTime timestamp;

    public AuditRecord() {
    }

    public AuditRecord(String methodName, String parametersInfo, long executionTimeMs, LocalDateTime timestamp) {
        this.methodName = methodName;
        this.parametersInfo = parametersInfo;
        this.executionTimeMs = executionTimeMs;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getParametersInfo() {
        return parametersInfo;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
