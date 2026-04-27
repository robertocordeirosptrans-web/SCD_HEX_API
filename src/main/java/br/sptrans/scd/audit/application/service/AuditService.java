package br.sptrans.scd.audit.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.audit.application.port.in.AuditUseCase;
import br.sptrans.scd.audit.application.port.out.AuditLogRepository;
import br.sptrans.scd.audit.domain.AuditEvent;
import br.sptrans.scd.audit.domain.AuditLog;
import lombok.RequiredArgsConstructor;

/**
 * Serviço central de auditoria.
 *
 * <p>
 * Para cada evento:
 * </p>
 * <ol>
 * <li>Emite log estruturado com prefixo {@code [AUDIT]} via SLF4J/MDC</li>
 * <li>Persiste o registro na tabela {@code AUDIT_LOG}</li>
 * </ol>
 *
 * <p>
 * A persistência utiliza {@code REQUIRES_NEW} para garantir que o registro
 * seja salvo mesmo se a transação da chamada sofrer rollback.
 * </p>
 *
 * <p>
 * Erros de persistência são capturados e logados como {@code WARN} sem
 * propagar exceção para o fluxo principal.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuditService implements AuditUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository repository;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Override
    @Async("auditExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void audit(AuditEvent event) {
        // 1. Log estruturado [AUDIT] — sem dados sensíveis
        log.info("[AUDIT] action={} user={} target={} session={} ip={}",
                event.action(),
                event.userId() != null ? event.userId() : "anonymous",
                event.targetUserId() != null ? event.targetUserId() : "-",
                event.sessionId() != null ? event.sessionId() : "-",
                event.ipAddress() != null ? event.ipAddress() : "-");
        // 2. Persiste no banco apenas se não for ambiente local
        if (!"local".equalsIgnoreCase(activeProfile)) {
            try {
                AuditLog auditLog = AuditLog.from(event);
                repository.save(auditLog);
            } catch (Exception ex) {
                log.warn("[AUDIT] FALLBACK — falha ao persistir evento={}: {}",
                        event.action(), ex.getMessage());
            }
        } else {
            log.debug("[AUDIT] Evento não persistido (profile local): {}", event.action());
        }
    }
}
