package br.sptrans.scd.audit.domain;

import java.time.LocalDateTime;

/**
 * Modelo de domínio para um registro de auditoria.
 * Mapeado pela camada de persistência para a tabela {@code AUDIT_LOG}.
 */
public class AuditLog {

    private Long id;
    private Long userId;
    private Long targetUserId;
    private String sessionId;
    private AuditEventType action;
    private String details;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(Long userId, Long targetUserId, String sessionId,
                    AuditEventType action, String details,
                    String ipAddress, String userAgent) {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.sessionId = sessionId;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = LocalDateTime.now();
    }

    public static AuditLog from(AuditEvent event) {
        return new AuditLog(
                event.userId(),
                event.targetUserId(),
                event.sessionId(),
                event.action(),
                event.details(),
                event.ipAddress(),
                event.userAgent()
        );
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getId()             { return id; }
    public Long getUserId()         { return userId; }
    public Long getTargetUserId()   { return targetUserId; }
    public String getSessionId()    { return sessionId; }
    public AuditEventType getAction() { return action; }
    public String getDetails()      { return details; }
    public String getIpAddress()    { return ipAddress; }
    public String getUserAgent()    { return userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters (used by persistence mapper) ──────────────────────────────────────────────

    public void setId(Long id)                     { this.id = id; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
