package com.example.payment.web.controller;

import com.example.payment.infrastructure.persistence.entity.AuditLog;
import com.example.payment.infrastructure.persistence.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> getAudit(@RequestHeader("X-User-Id") UUID userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}