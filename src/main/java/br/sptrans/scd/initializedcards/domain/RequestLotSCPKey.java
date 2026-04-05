package br.sptrans.scd.initializedcards.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RequestLotSCPKey {

    private String codTipoCanal;
    private String codCanal;
    private Long nrSolicitacao;
    private Long idLote;
    private String flgFaseSolicitacao;
}
