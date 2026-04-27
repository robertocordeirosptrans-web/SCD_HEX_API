package br.sptrans.scd.audit.adapter.out.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.audit.adapter.out.jpa.entity.AuditLogEntityJpa;
import br.sptrans.scd.audit.application.port.out.AuditLogRepository;
import br.sptrans.scd.audit.domain.AuditLog;
import lombok.RequiredArgsConstructor;

/**
 * Adapter de saída — mapeia entre {@link AuditLog} (domínio) e
 * {@link AuditLogEntityJpa} (JPA), delegando ao Spring Data.
 */
@Repository
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;

    @Override
    public AuditLog save(AuditLog log) {
        AuditLogEntityJpa entity = toEntity(log);
        AuditLogEntityJpa saved  = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AuditLog> findByUserId(Long userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findBySessionId(String sessionId) {
        return jpaRepository.findBySessionIdOrderByCreatedAtDesc(sessionId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ── Mapeamento domínio → entidade ─────────────────────────────────────────

    private AuditLogEntityJpa toEntity(AuditLog log) {
        AuditLogEntityJpa e = new AuditLogEntityJpa();
        e.setUserId(log.getUserId());
        e.setTargetUserId(log.getTargetUserId());
        e.setSessionId(log.getSessionId());
        e.setAction(log.getAction());
        e.setEventDetails(log.getDetails());
        e.setIpAddress(log.getIpAddress());
        e.setUserAgent(log.getUserAgent());
        e.setCreatedAt(log.getCreatedAt() != null ? log.getCreatedAt() : LocalDateTime.now());
        return e;
    }

    // ── Mapeamento entidade → domínio ─────────────────────────────────────────

    private AuditLog toDomain(AuditLogEntityJpa e) {
        AuditLog log = new AuditLog(
                e.getUserId(),
                e.getTargetUserId(),
                e.getSessionId(),
                e.getAction(),
                e.getEventDetails(),
                e.getIpAddress(),
                e.getUserAgent()
        );
        log.setId(e.getId());
        log.setCreatedAt(e.getCreatedAt());
        return log;
    }
}
