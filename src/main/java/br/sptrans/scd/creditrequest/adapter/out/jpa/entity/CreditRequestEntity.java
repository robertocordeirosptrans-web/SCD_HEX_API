package br.sptrans.scd.creditrequest.adapter.out.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity para SolDistribuicoes (Pedidos).
 */
@Entity
@Table(name = "SOL_DISTRIBUICOES", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestEntity {

    @EmbeddedId
    private CreditRequestEntityKey id;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "COD_TIPO_DOCUMENTO", length = 50)
    private String codTipoDocumento;

    @Column(name = "COD_SITUACAO", length = 50)
    private String codSituacao;

    @Column(name = "COD_FORMA_PAGTO", length = 50)
    private String codFormaPagto;

    @Column(name = "DT_SOLICITACAO")
    private LocalDateTime dtSolicitacao;

    @Column(name = "DT_PREV_LIBERACAO")
    private LocalDateTime dtPrevLiberacao;

    @Column(name = "DT_ACEITE")
    private LocalDateTime dtAceite;

    @Column(name = "DT_CONFIRMA_PAGTO")
    private LocalDateTime dtConfirmaPagto;

    @Column(name = "DT_PAGTO_ECONOMICA")
    private LocalDateTime dtPagtoEconomica;

    @Column(name = "COD_USUARIO_PORTADOR", length = 100)
    private String codUsuarioPortador;

    @Column(name = "DT_LIBERACAO_EFETIVA")
    private LocalDateTime dtLiberacaoEfetiva;

    @Column(name = "COD_ENDERECO_ENTREGA", length = 50)
    private String codEnderecoEntrega;

    @Column(name = "NUM_LOTE", length = 50)
    private String numLote;

    @Column(name = "DT_FINANCEIRA")
    private LocalDateTime dtFinanceira;

    @Column(name = "VL_TOTAL", precision = 15, scale = 2)
    private BigDecimal vlTotal;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "FLG_CANC", length = 1)
    private String flgCanc;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "DT_ENVIO_HM")
    private LocalDateTime dtEnvioHm;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @Column(name = "FLG_BLOQ", length = 1)
    private String flgBloq;

    @OneToMany(mappedBy = "solicitacao", fetch = FetchType.LAZY)
    private List<CreditRequestItemsEntity> itens;
}
