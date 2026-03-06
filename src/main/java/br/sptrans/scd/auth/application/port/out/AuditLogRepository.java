package br.sptrans.scd.auth.application.port.out;

/**
 * Output Port — Abstração de auditoria. O domínio apenas chama este port; quem
 * implementa decide se vai logar em arquivo, banco, SIEM, etc.
 */
public interface AuditLogRepository {

    void logLoginSuccess(String username, String ipAddress);

    void logLoginFailed(String username, String ipAddress, String reason);

    void logAccountBlocked(String username, int attempts);
}
