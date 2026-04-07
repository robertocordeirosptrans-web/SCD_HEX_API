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
 * Chave composta para HisSolItemSituacoesJpaEntity. Representa a PK
 * (NUM_SOLICITACAO, NUM_SOLICITACAO_ITEM, COD_CANAL, SEQ_HIST_SDIS) de
 * HIS_SOL_ITEM_SITUACOES.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HistCreditRequestItemsKeyEJpa implements Serializable{

    @Column(name = "NUM_SOLICITACAO", nullable = false)
    private Long numSolicitacao;

    @Column(name = "NUM_SOLICITACAO_ITEM", nullable = false)
    private Long numSolicitacaoItem;

    @Column(name = "COD_CANAL", nullable = false, length = 20)
    private String codCanal;

    @Column(name = "SEQ_HIST_SDIS", nullable = false)
    private Long seqHistSdis;
}
