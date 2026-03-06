package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.auth.domain.session.UserSession;

public interface UserSessionRepository {
    UserSession save(UserSession session);
    Optional<UserSession> findBySessionId(String sessionId);
    List<UserSession> findActiveByUserId(Long userId);  // para limite de sessões
    void revokeAllByUserId(Long userId, String reason); // logout total / troca de senha
}