package br.sptrans.scd.auth.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA — tabela USER_SESSIONS.
 * Armazena as sessões de autenticação dos usuários, permitindo
 * controle de revogação, expiração e rastreabilidade.
 */
@Entity
@Table(name = "USER_SESSIONS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionEntityJpa {

    @Id
    @Column(name = "ID_SESSAO", length = 36, nullable = false)
    private String idSessao;

    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;

    @Column(name = "ENDERECO_IP", length = 45)
    private String enderecoIp;

    @Column(name = "AGENTE_USUARIO", length = 500)
    private String agenteUsuario;

    @Column(name = "DT_CRIACAO", nullable = false)
    private LocalDateTime dtCriacao;

    @Column(name = "DT_EXPIRACAO", nullable = false)
    private LocalDateTime dtExpiracao;

    /** Preenchido quando a sessão é revogada ou expirada pelo job */
    @Column(name = "DT_REVOGACAO")
    private LocalDateTime dtRevogacao;

    /** LOGOUT | ADMIN_REVOKE | PASSWORD_CHANGED | EXPIRED | MAX_SESSIONS_EXCEEDED */
    @Column(name = "MOTIVO_REVOGACAO", length = 100)
    private String motivoRevogacao;

    /** ID do usuário que executou a revogação (NULL para expiração automática) */
    @Column(name = "ID_REVOGADO_POR")
    private Long idRevogadoPor;
}
