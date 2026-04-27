package br.sptrans.scd.initializedcards.adapter.in.rest.response;

import java.time.LocalDateTime;

import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoResponse {

    private Long seqHistSolicCartaoIni;
    private Long nrSolicitacao;
    private String flgFaseSolicitacao;
    private String stSolicitaCartaoInicializad;
    private LocalDateTime dtSolicitacao;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtCancelamento;

    public static HistoricoResponse fromDomain(HistRequestInitializedCards hist) {
        return HistoricoResponse.builder()
                .seqHistSolicCartaoIni(hist.getSeqHistSolicCartaoIni())
                .nrSolicitacao(hist.getNrSolicitacao())
                .flgFaseSolicitacao(hist.getFlgFaseSolicitacao())
                .stSolicitaCartaoInicializad(hist.getStSolicitaCartaoInicializad())
                .dtSolicitacao(hist.getDtSolicitacao())
                .dtCadastro(hist.getDtCadastro())
                .dtCancelamento(hist.getDtCancelamento())
                .build();
    }
}
