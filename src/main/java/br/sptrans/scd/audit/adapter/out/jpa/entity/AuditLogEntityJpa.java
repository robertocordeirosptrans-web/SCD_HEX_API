package br.sptrans.scd.audit.adapter.out.jpa.entity;

import java.time.LocalDateTime;

import br.sptrans.scd.audit.domain.AuditEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Entidade JPA — tabela {@code AUDIT_LOG} (schema SPTRANSDBA).
 * Cada linha representa um evento auditável ocorrido na API.
 *
 * <p>Campos {@code EVENT_DETAILS} e {@code USER_AGENT} são CLOB/VARCHAR2 extensos
 * para comportar JSON e strings longas de User-Agent.</p>
 */
@Entity
@Table(name = "AUDIT_API", schema = "SPTRANSDBA")
public class AuditLogEntityJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_log_gen")
    @SequenceGenerator(name = "audit_log_gen", sequenceName = "SPTRANSDBA.SCD_AUDIT_LOG_SEQ",
                       allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "TARGET_USER_ID")
    private Long targetUserId;

    @Column(name = "SESSION_ID", length = 100)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION", length = 50, nullable = false)
    private AuditEventType action;

    @Column(name = "EVENT_DETAILS", length = 4000)
    private String eventDetails;

    @Column(name = "IP_ADDRESS", length = 50)
    private String ipAddress;

    @Column(name = "USER_AGENT", length = 500)
    private String userAgent;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AuditLogEntityJpa() {
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getId()             { return id; }
    public Long getUserId()         { return userId; }
    public Long getTargetUserId()   { return targetUserId; }
    public String getSessionId()    { return sessionId; }
    public AuditEventType getAction() { return action; }
    public String getEventDetails() { return eventDetails; }
    public String getIpAddress()    { return ipAddress; }
    public String getUserAgent()    { return userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setId(Long id)                          { this.id = id; }
    public void setUserId(Long userId)                  { this.userId = userId; }
    public void setTargetUserId(Long targetUserId)      { this.targetUserId = targetUserId; }
    public void setSessionId(String sessionId)          { this.sessionId = sessionId; }
    public void setAction(AuditEventType action)        { this.action = action; }
    public void setEventDetails(String eventDetails)    { this.eventDetails = eventDetails; }
    public void setIpAddress(String ipAddress)          { this.ipAddress = ipAddress; }
    public void setUserAgent(String userAgent)          { this.userAgent = userAgent; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt = createdAt; }
}
