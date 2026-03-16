package br.sptrans.scd.initializedcards.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HistRequestInitializedCards {

    private String codTipoCanal;
    private SalesChannel codCanal;
    private Long nrSolicitacao;
    private Long seqHistSolicCartaoIni;// SEQ_HIST_SOLIC_CARTAO_INI
    private String codAdquirente;
    private Product codProduto;
    private Long qtdSolicitada;
    private Long qtdAtendida;
    private Long qtdRecebida;
    private String flgTipoSaida;
    private String flgTipoVolume;
    private String flgAssociaUsuario;
    private String flgGerarArquivo;
    private String flgRespEntregaRetirada;
    private String desRespEntrega;
    private String codTipoRespEntrega;
    private String codDoctoRespEntrega;
    private String codEnderecoEntrega;
    private String desRespRecebimento;
    private String codTipoDoctoRespRecebe;
    private String codDoctoRespRecebe;
    private String desMotivoDiferencaRecebe;
    private String flgAprovado;
    private User idUsuarioAprovacao;
    private LocalDateTime dtPrevistaEntrega;
    private LocalDateTime dtSolicitacao;
    private LocalDateTime dtAprovacao;
    private LocalDateTime dtAssociacaoLoteSCP;
    private LocalDateTime dtGeracaoArquivo;
    private LocalDateTime dtAssociacaoUsuario;
    private LocalDateTime dtEnvio;
    private LocalDateTime dtRecebimento;
    private LocalDateTime dtDevolucao;
    private String flgFaseSolicitacao;
    private String stSolicitaCartaoInicializad;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private LocalDateTime dtCancelamento;
    private User idUsuarioCadastro;
    private User idUsuarioManutencao;
}
