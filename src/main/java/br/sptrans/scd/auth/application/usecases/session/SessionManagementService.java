package br.sptrans.scd.auth.application.usecases.session;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.sptrans.scd.audit.application.port.in.AuditUseCase;
import br.sptrans.scd.audit.domain.AuditEvent;
import br.sptrans.scd.audit.domain.AuditEventType;
import br.sptrans.scd.auth.application.port.in.SessionManagementUseCase;
import br.sptrans.scd.auth.application.port.out.UserSessionRepository;
import br.sptrans.scd.auth.domain.session.UserSession;
import br.sptrans.scd.shared.audit.AuditContext;
import br.sptrans.scd.shared.audit.Auditable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do use case de gerenciamento de sessões.
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Criar sessão no login, respeitando o limite configurável de sessões simultâneas</li>
 *   <li>Revogar sessão individual (logout, admin)</li>
 *   <li>Revogar todas as sessões de um usuário</li>
 *   <li>Marcar sessões expiradas (chamado pelo job agendado)</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class SessionManagementService implements SessionManagementUseCase {

    private static final Logger log = LoggerFactory.getLogger(SessionManagementService.class);

    private final UserSessionRepository sessionRepository;
    private final AuditUseCase auditUseCase;

    @Value("${session.max-concurrent:5}")
    private int maxConcurrentSessions;

    @Value("${api.security.token.expiration-hours:2}")
    private int sessionTtlHours;

    @Override
    @Transactional
    public UserSession createSession(Long userId, String ip, String userAgent) {
        // Verifica limite de sessões simultâneas — revoga a mais antiga se excedido
        List<UserSession> activeSessions = sessionRepository.findActiveByUserId(userId);
        if (activeSessions.size() >= maxConcurrentSessions) {
            activeSessions.stream()
                    .min(Comparator.comparing(UserSession::getDtCriacao))
                    .ifPresent(oldest -> {
                        log.info("Limite de sessões atingido para usuario={}. Revogando sessão mais antiga: {}",
                                userId, oldest.getIdSessao());
                        sessionRepository.revokeBySessionId(
                                oldest.getIdSessao(), null, "MAX_SESSIONS_EXCEEDED");
                    });
        }

        UserSession session = new UserSession(userId, ip, userAgent, sessionTtlHours);
        UserSession saved = sessionRepository.save(session);
        log.info("Nova sessão criada: id={}, usuario={}, ip={}", saved.getIdSessao(), userId, ip);

        AuditContext.Data ctx = AuditContext.get();
        auditUseCase.audit(AuditEvent.of(
                AuditEventType.SESSION_CREATED,
                userId, saved.getIdSessao(),
                ctx.ipAddress, ctx.userAgent, null));
        return saved;
    }

    @Override
    @Transactional
    @Auditable(action = AuditEventType.SESSION_REVOKED, targetIdParam = "revokedBy")
    public void revokeSession(String sessionId, Long revokedBy, String reason) {
        sessionRepository.revokeBySessionId(sessionId, revokedBy, reason);
    }

    @Override
    @Transactional
    public void revokeAllUserSessions(Long userId, String reason) {
        sessionRepository.revokeAllByUserId(userId, reason);
        AuditContext.Data ctx = AuditContext.get();
        auditUseCase.audit(new AuditEvent(
                AuditEventType.SESSION_REVOKED,
                ctx.userId,
                userId,
                null,
                ctx.ipAddress,
                ctx.userAgent,
                "{\"reason\":\"REVOKE_ALL\",\"targetUserId\":" + userId + "}"));
    }

    @Override
    @Transactional
    public int expireSessions() {
        int count = sessionRepository.expireSessions();
        if (count > 0) {
            auditUseCase.audit(AuditEvent.preAuth(
                    AuditEventType.SESSION_EXPIRED,
                    null, null,
                    "{\"count\":" + count + "}"));
        }
        return count;
    }
}
