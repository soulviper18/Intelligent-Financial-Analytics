package com.example.demo.security;

import com.example.demo.service.AuditLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuditSecurityFilter extends OncePerRequestFilter {

    private final AuditLogService auditLogService;

    public AuditSecurityFilter(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(request, response);

        int status = response.getStatus();

        // Only log security-related failures
        if (status == 401 || status == 403) {

            String email = request.getUserPrincipal() != null
                    ? request.getUserPrincipal().getName()
                    : "ANONYMOUS";

            String path = request.getRequestURI();
            String method = request.getMethod();
            String ip = request.getRemoteAddr();

            String description = String.format(
                    "Blocked request | %s %s | IP: %s | Status: %d",
                    method, path, ip, status
            );

            auditLogService.logEvent(
                    email,
                    status == 401 ? "UNAUTHORIZED_ACCESS" : "FORBIDDEN_ACCESS",
                    description
            );
        }
    }
}
