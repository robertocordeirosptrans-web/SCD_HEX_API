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

    private String idSessao;         // UUID — mesmo que vai no JWT claim
    private Long   idUsuario;        // FK para o usuário dono da sessão
    private String enderecoIp;       // IP de origem do login
    private String agenteUsuario;    // Navegador/dispositivo
    private LocalDateTime dtCriacao;
    private LocalDateTime dtExpiracao;
    private LocalDateTime dtRevogacao; // null = sessão ainda válida
    private String motivoRevogacao;    // "LOGOUT", "ADMIN_REVOKE", "PASSWORD_CHANGED"
    private Long idRevogadoPor;        // ID do usuário que revogou (null se expiração automática)

    // Construtor chamado no momento do login
    public UserSession(Long idUsuario, String enderecoIp, String agenteUsuario, int ttlHoras) {
        this.idSessao      = UUID.randomUUID().toString();
        this.idUsuario     = idUsuario;
        this.enderecoIp    = enderecoIp;
        this.agenteUsuario = agenteUsuario;
        this.dtCriacao     = LocalDateTime.now();
        this.dtExpiracao   = LocalDateTime.now().plusHours(ttlHoras);
        this.dtRevogacao   = null;
        this.idRevogadoPor = null;
    }

    /** Status computado da sessão */
    public SessionStatus getStatus() {
        if (dtRevogacao != null) return SessionStatus.REVOKED;
        if (LocalDateTime.now().isAfter(dtExpiracao)) return SessionStatus.EXPIRED;
        return SessionStatus.ACTIVE;
    }

    /** Sessão válida = não expirada E não revogada */
    public boolean isAtiva() {
        return getStatus() == SessionStatus.ACTIVE;
    }

    /** Revoga a sessão com motivo e quem revogou */
    public void revogar(String motivo, Long revokedBy) {
        this.dtRevogacao     = LocalDateTime.now();
        this.motivoRevogacao = motivo;
        this.idRevogadoPor   = revokedBy;
    }

    /** Logout do usuário — revoga a sessão sem rastrear executor */
    public void revogar(String motivo) {
        revogar(motivo, null);
    }
}