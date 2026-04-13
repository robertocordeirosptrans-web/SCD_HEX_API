package br.sptrans.scd.audit.domain;

/**
 * Objeto de valor imutável que transporta todos os dados de um evento de auditoria.
 * Passado ao {@link br.sptrans.scd.audit.application.port.in.AuditUseCase} para
 * persistência e logging.
 *
 * @param action       tipo do evento
 * @param userId       ID do usuário executor (null se não autenticado)
 * @param targetUserId ID do usuário alvo da ação (null se não aplicável)
 * @param sessionId    ID da sessão corrente (null se pré-autenticação)
 * @param ipAddress    endereço IP de origem
 * @param userAgent    User-Agent do cliente
 * @param details      informações adicionais em formato JSON (sem dados sensíveis)
 */
public record AuditEvent(
        AuditEventType action,
        Long userId,
        Long targetUserId,
        String sessionId,
        String ipAddress,
        String userAgent,
        String details
) {

    /** Atalho quando não há usuário alvo. */
    public static AuditEvent of(AuditEventType action, Long userId,
                                String sessionId, String ipAddress,
                                String userAgent, String details) {
        return new AuditEvent(action, userId, null, sessionId, ipAddress, userAgent, details);
    }

    /** Atalho para eventos pré-autenticação (sem sessão). */
    public static AuditEvent preAuth(AuditEventType action, String ipAddress,
                                     String userAgent, String details) {
        return new AuditEvent(action, null, null, null, ipAddress, userAgent, details);
    }
}
