package br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SOLICITA_CARTAO_INICIALIZADO", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RICEntityJpa {

    @EmbeddedId
    private RICEntityJpaKey id;

    @Column(name = "COD_ADQUIRENTE", length = 20)
    private String codAdquirente;

    @Column(name = "COD_PRODUTO", length = 20)
    private String codProduto;

    @Column(name = "QTD_SOLICITADA", nullable = false)
    private Long qtdSolicitada;

    @Column(name = "QTD_ATENDIDA")
    private Long qtdAtendida;

    @Column(name = "QTD_RECEBIDA")
    private Long qtdRecebida;

    @Column(name = "FLG_TIPO_SAIDA", length = 1, nullable = false)
    private String flgTipoSaida;

    @Column(name = "FLG_TIPO_VOLUME", length = 1, nullable = false)
    private String flgTipoVolume;

    @Column(name = "FLG_ASSOCIACAO_USUARIO", length = 1, nullable = false)
    private String flgAssociacaoUsuario;

    @Column(name = "FLG_GERA_ARQUIVO", length = 1, nullable = false)
    private String flgGeraArquivo;

    @Column(name = "FLG_RESP_ENTREGA_RETIRADA", length = 1)
    private String flgRespEntregaRetirada;

    @Column(name = "DES_NOME_RESP_ENTREGA", length = 60)
    private String desNomeRespEntrega;

    @Column(name = "COD_TIPO_DOCTO_RESP_ENTREGA", length = 3)
    private String codTipoDoctoRespEntrega;

    @Column(name = "COD_DOCTO_RESP_ENTREGA", length = 20)
    private String codDoctoRespEntrega;

    @Column(name = "COD_ENDERECO_ENTREGA", length = 20)
    private String codEnderecoEntrega;

    @Column(name = "DES_NOME_RESP_RECEBIMENTO", length = 60)
    private String desNomeRespRecebimento;

    @Column(name = "COD_TIPO_DOCTO_RESP_RECEBE", length = 3)
    private String codTipoDoctoRespRecebe;

    @Column(name = "COD_DOCTO_RESP_RECEBE", length = 20)
    private String codDoctoRespRecebe;

    @Column(name = "DES_MOTIVO_DIFERENCA_RECEBE", length = 60)
    private String desMotivoDiferencaRecebe;

    @Column(name = "FLG_APROVADO", length = 1)
    private String flgAprovado;

    @Column(name = "ID_USUARIO_APROVACAO")
    private Long idUsuarioAprovacao;

    @Column(name = "DT_PREVISTA_ENTREGA")
    private Date dtPrevistaEntrega;

    @Column(name = "DT_SOLICITACAO")
    private Date dtSolicitacao;

    @Column(name = "DT_APROVACAO")
    private Date dtAprovacao;

    @Column(name = "DT_ASSOCIACAO_LOTE_SCP")
    private Date dtAssociacaoLoteScp;

    @Column(name = "DT_GERACAO_ARQUIVO")
    private Date dtGeracaoArquivo;

    @Column(name = "DT_ASSOCIACAO_USUARIO")
    private Date dtAssociacaoUsuario;

    @Column(name = "DT_ENVIO")
    private Date dtEnvio;

    @Column(name = "DT_RECEBIMENTO")
    private Date dtRecebimento;

    @Column(name = "DT_DEVOLUCAO")
    private Date dtDevolucao;

    @Column(name = "FLG_FASE_SOLICITACAO", length = 1)
    private String flgFaseSolicitacao;

    @Column(name = "ST_SOLICITA_CARTAO_INICIALIZAD", length = 1)
    private String stSolicitaCartaoInicializad;

    @Column(name = "DT_CADASTRO")
    private Date dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private Date dtManutencao;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @Column(name = "DT_CANCELAMENTO")
    private Date dtCancelamento;
}
