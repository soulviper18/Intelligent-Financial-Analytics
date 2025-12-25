package com.example.demo.controller;

import com.example.demo.model.AuditLog;
import com.example.demo.service.AuditLogService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // 🔹 GET ALL LOGS
    @GetMapping
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogService.getAllLogs(pageable);
    }

    // 🔹 GET LOGS BY EMAIL
    @GetMapping("/user/{email}")
    public Page<AuditLog> getLogsByUser(
            @PathVariable String email,
            Pageable pageable) {

        return auditLogService.getLogsByEmail(email, pageable);
    }
}
