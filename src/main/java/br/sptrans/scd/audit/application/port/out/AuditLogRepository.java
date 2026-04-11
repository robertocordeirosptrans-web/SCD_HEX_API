package br.sptrans.scd.audit.application.port.out;

import java.util.List;

import br.sptrans.scd.audit.domain.AuditLog;

/**
 * Output Port — persistência de {@link AuditLog}.
 * Implementado por {@link br.sptrans.scd.audit.adapter.out.jpa.AuditLogPersistenceAdapter}.
 */
public interface AuditLogRepository {

    AuditLog save(AuditLog log);

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findBySessionId(String sessionId);
}
