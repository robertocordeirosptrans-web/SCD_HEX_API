package br.sptrans.scd.initializedcards.adapter.in.rest.response;

import java.time.LocalDateTime;

import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoResponse {

    private String codTipoCanal;
    private String codCanal;
    private String desCanal;
    private String desClassificacaoPessoa;
    private Long nrSolicitacao;
    private String codAdquirente;
    private String codProduto;
    private String desProduto;
    private Long qtdSolicitada;
    private Long qtdAtendida;
    private Long qtdRecebida;
    private String flgTipoSaida;
    private String flgTipoVolume;
    private String flgAssociacaoUsuario;
    private String flgGeraArquivo;
    private String flgAprovado;
    private String desNomeRespEntrega;
    private String codTipoDoctoRespEntrega;
    private String codDoctoRespEntrega;
    private String desNomeRespRecebimento;
    private String codTipoDoctoRespRecebe;
    private String codDoctoRespRecebe;
    private LocalDateTime dtPrevistaEntrega;
    private LocalDateTime dtSolicitacao;
    private String flgFaseSolicitacao;
    private String stSolicitaCartaoInicializad;
    private LocalDateTime dtCancelamento;

    public static SolicitacaoResponse fromDomain(RequestInitializedCards domain) {
        return SolicitacaoResponse.builder()
                .codTipoCanal(domain.getCodTipoCanal())
                .codCanal(domain.getCodCanal() != null ? domain.getCodCanal().getCodCanal() : null)
                .desCanal(domain.getCodCanal() != null ? domain.getCodCanal().getDesCanal() : null)
                .desClassificacaoPessoa(domain.getCodCanal() != null
                        && domain.getCodCanal().getCodClassificacaoPessoa() != null
                        ? domain.getCodCanal().getCodClassificacaoPessoa().getDesClassificacaoPessoa()
                        : null)
                .nrSolicitacao(domain.getNrSolicitacao())
                .codAdquirente(domain.getCodAdquirente())
                .codProduto(domain.getCodProduto() != null ? domain.getCodProduto().getCodProduto() : null)
                .desProduto(domain.getCodProduto() != null ? domain.getCodProduto().getDesProduto() : null)
                .qtdSolicitada(domain.getQtdSolicitada())
                .qtdAtendida(domain.getQtdAtendida())
                .qtdRecebida(domain.getQtdRecebida())
                .flgTipoSaida(domain.getFlgTipoSaida())
                .flgTipoVolume(domain.getFlgTipoVolume())
                .flgAssociacaoUsuario(domain.getFlgAssociaUsuario())
                .flgGeraArquivo(domain.getFlgGerarArquivo())
                .flgAprovado(domain.getFlgAprovado())
                .desNomeRespEntrega(domain.getDesRespEntrega())
                .codTipoDoctoRespEntrega(domain.getCodTipoRespEntrega())
                .codDoctoRespEntrega(domain.getCodDoctoRespEntrega())
                .desNomeRespRecebimento(domain.getDesRespRecebimento())
                .codTipoDoctoRespRecebe(domain.getCodTipoDoctoRespRecebe())
                .codDoctoRespRecebe(domain.getCodDoctoRespRecebe())
                .dtPrevistaEntrega(domain.getDtPrevistaEntrega())
                .dtSolicitacao(domain.getDtSolicitacao())
                .flgFaseSolicitacao(domain.getFlgFaseSolicitacao())
                .stSolicitaCartaoInicializad(domain.getStSolicitaCartaoInicializad())
                .dtCancelamento(domain.getDtCancelamento())
                .build();
    }
}
