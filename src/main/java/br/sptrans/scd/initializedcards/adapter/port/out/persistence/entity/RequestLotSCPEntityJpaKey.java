package br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity;

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
public class RequestLotSCPEntityJpaKey implements Serializable{

    @Column(name = "COD_TIPO_CANAL", length = 20, nullable = false)
    private String codTipoCanal;

    @Column(name = "COD_CANAL", length = 20, nullable = false)
    private String codCanal;

    @Column(name = "NR_SOLICITACAO", nullable = false)
    private Long nrSolicitacao;

    @Column(name = "ID_LOTE", nullable = false)
    private Long idLote;

    @Column(name = "FLG_FASE_SOLICITACAO", length = 1, nullable = false)
    private String flgFaseSolicitacao;
}
