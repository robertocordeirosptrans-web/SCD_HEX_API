package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.auth.domain.session.UserSession;

public interface UserSessionRepository {
    UserSession save(UserSession session);
    Optional<UserSession> findBySessionId(String sessionId);
    List<UserSession> findActiveByUserId(Long userId);
    void revokeAllByUserId(Long userId, String reason);

    /** Revoga uma sessão específica e registra quem revogou */
    void revokeBySessionId(String sessionId, Long revokedBy, String reason);

    /**
     * Marca como expiradas todas as sessões cujo DT_EXPIRACAO já passou.
     * Chamado pelo job agendado.
     * @return número de sessões expiradas
     */
    int expireSessions();
}