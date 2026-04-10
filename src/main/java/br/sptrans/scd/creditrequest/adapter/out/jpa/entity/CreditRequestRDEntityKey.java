package br.sptrans.scd.creditrequest.adapter.out.jpa.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreditRequestRDEntityKey implements Serializable {

    @Column(name = "COD_CANAL", nullable = false, length = 20)
    private String codCanal;

    @Column(name = "NUM_SOLICITACAO", nullable = false, length = 20)
    private Long numSolicitacao;

    @Column(name = "COD_CANAL_DISTRIBUICAO", nullable = false, length = 20)
    private String codCanalDistribuicao;
}
