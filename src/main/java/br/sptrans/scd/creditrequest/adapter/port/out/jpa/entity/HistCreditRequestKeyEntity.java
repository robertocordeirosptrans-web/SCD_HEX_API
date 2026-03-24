package br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@RequiredArgsConstructor
public class HistCreditRequestKeyEntity implements Serializable {
    @Column(name = "NUM_SOLICITACAO")
    private Long numSolicitacao;

    @Column(name = "COD_CANAL")
    private String codCanal;

    @Column(name = "SEQ_HIST_SDS")
    private Long seqHistSdis;


}
