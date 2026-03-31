package br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity para SolDistribItens (Itens do Pedido).
 */
@Entity
@Table(name = "SOL_DISTRIB_ITENS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestItemsEJpa {

    @EmbeddedId
    private CreditRequestItemsEJpaKey id;

    @Column(name = "COD_CANAL", length = 50, insertable = false, updatable = false)
    private String codCanal;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "COD_VERSAO", length = 50)
    private String codVersao;

    @Column(name = "NUM_LOGICO_CARTAO", length = 100)
    private String numLogicoCartao;

    @Column(name = "COD_PRODUTO", length = 50)
    private String codProduto;

    @Column(name = "COD_TIPO_DOCUMENTO", length = 50)
    private String codTipoDocumento;

    @Column(name = "COD_SITUACAO", length = 50)
    private String codSituacao;

    @Column(name = "QTD_ITEM")
    private Integer qtdItem;

    @Column(name = "VL_UNITARIO", precision = 15, scale = 2)
    private BigDecimal vlUnitario;

    @Column(name = "VL_ITEM", precision = 15, scale = 2)
    private BigDecimal vlItem;

    @Column(name = "DT_RECARGA")
    private LocalDateTime dtRecarga;

    @Column(name = "VL_CARREGADO", precision = 15, scale = 2)
    private BigDecimal vlCarregado;

    @Column(name = "VL_AJUSTE", precision = 15, scale = 2)
    private BigDecimal vlAjuste;

    @Column(name = "FLG_AJUSTE", length = 1)
    private String flgAjuste;

    @Column(name = "ID_FUNCIONARIO", length = 50)
    private String idFuncionario;

    // @Column(name = "COD_ASSINATURA_HSM", length = 500)
    // private String codAssinaturaHsm;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "SEQ_RECARGA")
    private Integer seqRecarga;

    @Column(name = "DT_ENVIO_HM")
    private LocalDateTime dtEnvioHm;

    @Column(name = "DT_RETORNO_HM")
    private LocalDateTime dtRetornoHm;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @Column(name = "DT_ASSINATURA")
    private LocalDateTime dtAssinatura;

    @Column(name = "DT_PAGTO_ECONOMICA")
    private LocalDateTime dtPagtoEconomica;

    @Column(name = "SQ_PID")
    private Long sqPid;

    @Column(name = "DT_INIC_PROCESSO")
    private LocalDateTime dtInicProcesso;

    @Column(name = "ID_USUARIO_CARTAO")
    private Long idUsuarioCartao;

    @Column(name = "SQ_RECARGA")
    private Integer sqRecarga;

    @Column(name = "VL_TXADM", precision = 15, scale = 2)
    private BigDecimal vlTxadm;

    @Column(name = "VL_TXSERV", precision = 15, scale = 2)
    private BigDecimal vlTxserv;

    @Column(name = "VL_TXTOTAL", precision = 15, scale = 2)
    private BigDecimal vlTxtotal;

    @Column(name = "FLG_EVENTO", length = 1)
    private String flgEvento;

    @Column(name = "VL_EVENTO", precision = 15, scale = 2)
    private BigDecimal vlEvento;

    @Column(name = "FLG_OUTRAS_VIAS", length = 1)
    private String flgOutrasVias;


    @Column(name = "VL_AUTORIZACAO_HM", precision = 15, scale = 2)
    private BigDecimal vlAutorizacaoHm;

    @Column(name = "FLG_LIMINAR_LOJA")
    private Integer flgLiminarLoja;

    @Column(name = "COD_PRODUTO_HM", length = 50)
    private String codProdutoHm;

    @Column(name = "QTD_DIAS_UTILIZADOS")
    private Integer qtdDiasUtilizados;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "NUM_SOLICITACAO", referencedColumnName = "NUM_SOLICITACAO", insertable = false, updatable = false),
        @JoinColumn(name = "COD_CANAL", referencedColumnName = "COD_CANAL", insertable = false, updatable = false)
    })
    private CreditRequestEJpa solicitacao;
}
