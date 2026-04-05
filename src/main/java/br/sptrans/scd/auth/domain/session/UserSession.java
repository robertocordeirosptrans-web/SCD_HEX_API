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

    // Construtor chamado no momento do login
    public UserSession(Long idUsuario, String enderecoIp, String agenteUsuario, int ttlHoras) {
        this.idSessao      = UUID.randomUUID().toString();
        this.idUsuario     = idUsuario;
        this.enderecoIp    = enderecoIp;
        this.agenteUsuario = agenteUsuario;
        this.dtCriacao     = LocalDateTime.now();
        this.dtExpiracao   = LocalDateTime.now().plusHours(ttlHoras);
        this.dtRevogacao   = null;
    }

    /** Sessão válida = não expirada E não revogada */
    public boolean isAtiva() {
        return dtRevogacao == null && LocalDateTime.now().isBefore(dtExpiracao);
    }

    /** Logout do usuário — revoga a sessão */
    public void revogar(String motivo) {
        this.dtRevogacao    = LocalDateTime.now();
        this.motivoRevogacao = motivo;
    }

   
}