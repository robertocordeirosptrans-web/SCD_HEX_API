package br.sptrans.scd.creditrequest.adapter.out.jpa.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Chave composta para HisSolSituacoesJpaEntity.
 * Representa a PK (NUM_SOLICITACAO, COD_CANAL, SEQ_HIST_SDS)
 * de HIS_SOL_SITUACOES.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HistCreditRequestEntityKey implements Serializable {

    @Column(name = "NUM_SOLICITACAO", nullable = false)
    private Long numSolicitacao;

    @Column(name = "COD_CANAL", nullable = false, length = 20)
    private String codCanal;

    @Column(name = "SEQ_HIST_SDS", nullable = false)
    private Long seqHistSdis;
}
