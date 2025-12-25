package com.example.demo.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String action;
    private String description;

    private Instant timestamp;

    // getters & setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getAction() { return action; }
    public String getDescription() { return description; }
    public Instant getTimestamp() { return timestamp; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setAction(String action) { this.action = action; }
    public void setDescription(String description) { this.description = description; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
