package com.enterprise.engine.aop;

import com.enterprise.engine.domain.AuditRecord;
import com.enterprise.engine.repository.AuditRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    private final AuditRepository auditRepository;

    public AuditAspect(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Around("@annotation(AuditLog)")
    public Object logExecutionTimeAndDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        // Sanitize and serialize args securely
        String args = Arrays.toString(joinPoint.getArgs());
        if (args.length() > 255) {
            args = args.substring(0, 250) + "...";
        }

        AuditRecord auditRecord = new AuditRecord(methodName, args, executionTime, LocalDateTime.now());
        auditRepository.save(auditRecord);

        return proceed;
    }
}
