package br.sptrans.scd.initializedcards.adapter.in.rest.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.sptrans.scd.initializedcards.application.port.in.RequestInitializedUseCase.SolicitacaoDetalhe;
import br.sptrans.scd.initializedcards.domain.TbLotSCD;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoDetalheResponse {

    private String codTipoCanal;
    private String codCanal;
    private Long nrSolicitacao;
    private String codAdquirente;
    private String codProduto;
    private Long qtdSolicitada;
    private Long qtdAtendida;
    private Long qtdRecebida;
    private String flgTipoSaida;
    private String flgTipoVolume;
    private String flgAssociacaoUsuario;
    private String flgGeraArquivo;
    private String flgAprovado;
    private LocalDateTime dtPrevistaEntrega;
    private LocalDateTime dtSolicitacao;
    private String flgFaseSolicitacao;
    private String stSolicitaCartaoInicializad;
    private LocalDateTime dtCancelamento;
    private List<LoteResponse> lotes;
    private List<HistoricoResponse> historico;

    public static SolicitacaoDetalheResponse fromDomain(SolicitacaoDetalhe detalhe, List<TbLotSCD> tbLotes) {
        var solicitacao = detalhe.solicitacao();

        Map<Long, TbLotSCD> tbLotMap = tbLotes.stream()
                .collect(Collectors.toMap(TbLotSCD::getIdLote, l -> l));

        List<LoteResponse> lotesResp = detalhe.lotes().stream()
                .map(l -> LoteResponse.fromDomain(l,
                        l.getId() != null ? tbLotMap.get(l.getId().getIdLote()) : null))
                .collect(Collectors.toList());

        List<HistoricoResponse> historicoResp = detalhe.historico().stream()
                .map(HistoricoResponse::fromDomain)
                .collect(Collectors.toList());

        return SolicitacaoDetalheResponse.builder()
                .codTipoCanal(solicitacao.getCodTipoCanal())
                .codCanal(solicitacao.getCodCanal() != null ? solicitacao.getCodCanal().getCodCanal() : null)
                .nrSolicitacao(solicitacao.getNrSolicitacao())
                .codAdquirente(solicitacao.getCodAdquirente())
                .codProduto(solicitacao.getCodProduto() != null ? solicitacao.getCodProduto().getCodProduto() : null)
                .qtdSolicitada(solicitacao.getQtdSolicitada())
                .qtdAtendida(solicitacao.getQtdAtendida())
                .qtdRecebida(solicitacao.getQtdRecebida())
                .flgTipoSaida(solicitacao.getFlgTipoSaida())
                .flgTipoVolume(solicitacao.getFlgTipoVolume())
                .flgAssociacaoUsuario(solicitacao.getFlgAssociaUsuario())
                .flgGeraArquivo(solicitacao.getFlgGerarArquivo())
                .flgAprovado(solicitacao.getFlgAprovado())
                .dtPrevistaEntrega(solicitacao.getDtPrevistaEntrega())
                .dtSolicitacao(solicitacao.getDtSolicitacao())
                .flgFaseSolicitacao(solicitacao.getFlgFaseSolicitacao())
                .stSolicitaCartaoInicializad(solicitacao.getStSolicitaCartaoInicializad())
                .dtCancelamento(solicitacao.getDtCancelamento())
                .lotes(lotesResp)
                .historico(historicoResp)
                .build();
    }
}
