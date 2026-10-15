package com.example.payment.infrastructure.service.audit;

import com.example.payment.infrastructure.persistence.entity.AuditLog;
import com.example.payment.infrastructure.persistence.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID userId, String action, String entityType, UUID entityId, String details) {
        try {
            AuditLog entry = AuditLog.builder()
                    .userId(userId)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .details(details)
                    .build();

            auditLogRepository.save(entry);
            log.info("AUDIT: {} by user {} on {}:{}", action, userId, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to save audit log: action={}, userId={}", action, userId, e);
        }
    }
}