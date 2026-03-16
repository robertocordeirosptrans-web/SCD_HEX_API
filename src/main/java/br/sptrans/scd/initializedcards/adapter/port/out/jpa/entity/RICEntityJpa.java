package br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity;

import jakarta.persistence.Column;
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
  @Column(name = "COD_TIPO_CANAL", length = 20, nullable = false)
  private String codTipoCanal;

  @Column(name = "COD_CANAL", length = 20, nullable = false)
  private String codCanal;

  @Column(name = "NR_SOLICITACAO", nullable = false)
  private Long nrSolicitacao;

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
  private java.util.Date dtPrevistaEntrega;

  @Column(name = "DT_SOLICITACAO")
  private java.util.Date dtSolicitacao;

  @Column(name = "DT_APROVACAO")
  private java.util.Date dtAprovacao;

  @Column(name = "DT_ASSOCIACAO_LOTE_SCP")
  private java.util.Date dtAssociacaoLoteScp;

  @Column(name = "DT_GERACAO_ARQUIVO")
  private java.util.Date dtGeracaoArquivo;

  @Column(name = "DT_ASSOCIACAO_USUARIO")
  private java.util.Date dtAssociacaoUsuario;

  @Column(name = "DT_ENVIO")
  private java.util.Date dtEnvio;

  @Column(name = "DT_RECEBIMENTO")
  private java.util.Date dtRecebimento;

  @Column(name = "DT_DEVOLUCAO")
  private java.util.Date dtDevolucao;

  @Column(name = "FLG_FASE_SOLICITACAO", length = 1)
  private String flgFaseSolicitacao;

  @Column(name = "ST_SOLICITA_CARTAO_INICIALIZAD", length = 1)
  private String stSolicitaCartaoInicializad;

  @Column(name = "DT_CADASTRO")
  private java.util.Date dtCadastro;

  @Column(name = "DT_MANUTENCAO")
  private java.util.Date dtManutencao;

  @Column(name = "ID_USUARIO_CADASTRO")
  private Long idUsuarioCadastro;

  @Column(name = "ID_USUARIO_MANUTENCAO")
  private Long idUsuarioManutencao;

  @Column(name = "DT_CANCELAMENTO")
  private java.util.Date dtCancelamento;
}
