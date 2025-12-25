package com.example.demo.service;

import com.example.demo.model.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // ---------------- CREATE LOG ----------------
    public void logEvent(String email, String action, String description) {

        AuditLog log = new AuditLog();
        log.setEmail(email);
        log.setAction(action);
        log.setDescription(description);

        auditLogRepository.save(log);
    }

    // ---------------- GET ALL LOGS (ADMIN) ----------------
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    // ---------------- GET LOGS BY EMAIL ----------------
    public Page<AuditLog> getLogsByEmail(String email, Pageable pageable) {
        return auditLogRepository.findByEmail(email, pageable);
    }
}

