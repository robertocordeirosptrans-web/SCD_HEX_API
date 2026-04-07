package br.sptrans.scd.creditrequest.adapter.out.jpa.entity;

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
 * JPA Entity para Situações (Order Statuses).
 *
 * Esta classe representa a entidade persistida no banco de dados. Contém todas
 * as anotações JPA necessárias para mapping. Separada da entidade de domínio
 * (domain.pedidos.Situacoes) para isolamento.
 */
@Entity
@Table(name = "SITUACOES", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SituationEJpa {

    @Id
    @Column(name = "COD_SITUACAO", length = 20)
    private String codSituacao;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @Column(name = "ATIVO", length = 1)
    private String ativo;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}
