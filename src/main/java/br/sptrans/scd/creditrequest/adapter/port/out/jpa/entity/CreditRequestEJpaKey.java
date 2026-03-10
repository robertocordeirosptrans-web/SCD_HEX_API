package br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class CreditRequestEJpaKey implements Serializable {

    @Column(name = "NUM_SOLICITACAO", nullable = false)
    private Long numSolicitacao;

    @Column(name = "COD_CANAL", nullable = false, length = 20)
    private String codCanal;
}
