package br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity;

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
 * JPA Entity para a tabela LOG_RECARGAS (adapter layer).
 *
 * <p>
 * Separada da entidade de domínio
 * {@link com.example.auth.domain.pedidos.LogRecargas} para manter o domínio
 * livre de dependências JPA (arquitetura hexagonal).</p>
 *
 * <p>
 * Mantém exatamente um registro por cartão (num_logico_cartao = PK).</p>
 */
@Entity
@Table(name = "LOG_RECARGAS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RechargeLogEJpa {

    @Id
    @Column(name = "NUM_LOGICO_CARTAO", nullable = false, length = 15)
    private String numLogicoCartao;

    @Column(name = "SEQ_RECARGA", nullable = false, length = 6)
    private Integer seqRecarga;

    @Column(name = "DT_SOLIC_RECARGA", nullable = false)
    private LocalDateTime dtSolicRecarga;

    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}
