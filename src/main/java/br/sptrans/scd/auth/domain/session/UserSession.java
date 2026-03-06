package br.sptrans.scd.auth.domain.session;



import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * Agregado de domínio: representa uma sessão ativa de um usuário.
 *
 * Resolveria o problema atual do SBE_SCD onde o sessionId no JWT
 * é apenas decorativo — aqui ele teria significado real.
 *
 * Casos de uso habilitados por esta classe:
 * - Logout real (invalidar token antes de expirar)
 * - Bloquear sessões duplicadas / limite por dispositivo
 * - Ver sessões ativas no painel do usuário
 * - Revogar sessão administrativa
 */
@Getter
@Setter
public class UserSession {

    private String sessionId;       // UUID — mesmo que vai no JWT claim
    private Long   userId;          // FK para o usuário dono da sessão
    private String ipAddress;       // IP de origem do login
    private String userAgent;       // Navegador/dispositivo
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt; // null = sessão ainda válida
    private String revokedReason;    // "LOGOUT", "ADMIN_REVOKE", "PASSWORD_CHANGED"

    // Construtor chamado no momento do login
    public UserSession(Long userId, String ipAddress, String userAgent, int ttlHours) {
        this.sessionId  = UUID.randomUUID().toString();
        this.userId     = userId;
        this.ipAddress  = ipAddress;
        this.userAgent  = userAgent;
        this.createdAt  = LocalDateTime.now();
        this.expiresAt  = LocalDateTime.now().plusHours(ttlHours);
        this.revokedAt  = null;
    }

    /** Sessão válida = não expirada E não revogada */
    public boolean isActive() {
        return revokedAt == null && LocalDateTime.now().isBefore(expiresAt);
    }

    /** Logout do usuário — revoga a sessão */
    public void revoke(String reason) {
        this.revokedAt     = LocalDateTime.now();
        this.revokedReason = reason;
    }

   
}