package br.sptrans.scd.auth.application.port.in;

import br.sptrans.scd.auth.domain.session.UserSession;

/**
 * Input Port — Gerenciamento de Sessões.
 * Define o contrato para criação, revogação e expiração de sessões.
 */
public interface SessionManagementUseCase {

    /**
     * Cria uma nova sessão para o usuário no momento do login.
     * Se o limite de sessões simultâneas for atingido, revoga a mais antiga.
     *
     * @param userId    ID do usuário autenticado
     * @param ip        endereço IP de origem
     * @param userAgent User-Agent do cliente
     * @return sessão criada e persistida
     */
    UserSession createSession(Long userId, String ip, String userAgent);

    /**
     * Revoga uma sessão específica (logout individual ou revogação administrativa).
     *
     * @param sessionId  ID da sessão a revogar
     * @param revokedBy  ID do usuário executor (null = sistema)
     * @param reason     motivo: LOGOUT, ADMIN_REVOKE, PASSWORD_CHANGED, etc.
     */
    void revokeSession(String sessionId, Long revokedBy, String reason);

    /**
     * Revoga todas as sessões ativas de um usuário
     * (troca de senha, bloqueio administrativo, etc.).
     *
     * @param userId ID do usuário alvo
     * @param reason motivo da revogação em massa
     */
    void revokeAllUserSessions(Long userId, String reason);

    /**
     * Job de expiração: marca no banco todas as sessões cujo prazo já passou.
     *
     * @return número de sessões expiradas
     */
    int expireSessions();
}
