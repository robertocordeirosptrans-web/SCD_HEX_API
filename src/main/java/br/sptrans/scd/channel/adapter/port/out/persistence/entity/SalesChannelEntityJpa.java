package br.sptrans.scd.channel.adapter.port.out.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CANAIS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesChannelEntityJpa {

    @Id
    @Column(name = "COD_CANAL", length = 20)
    private String codCanal;

    @Column(name = "COD_DOCUMENTO", length = 20)
    private String codDocumento;

    @Column(name = "COD_CANAL_SUPERIOR", length = 20)
    private String codCanalSuperior;

    @Column(name = "DES_CANAL", nullable = false, length = 60)
    private String desCanal;

    @Column(name = "COD_TIPO_DOCUMENTO", length = 4)
    private String codTipoDocumento;

    @Column(name = "DT_MANUTENCAO", nullable = false)
    private LocalDateTime dtManutencao;

    @Column(name = "DES_RAZAO_SOCIAL", length = 60)
    private String desRazaoSocial;

    @Column(name = "ST_CANAIS", length = 1)
    private String stCanais;

    @Column(name = "DES_NOME_FANTASIA", length = 60)
    private String desNomeFantasia;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "VL_CAUCAO", precision = 15, scale = 4)
    private BigDecimal vlCaucao;

    @Column(name = "DT_INICIO_CAUCAO")
    private LocalDate dtInicioCaucao;

    @Column(name = "DT_FIM_CAUCAO")
    private LocalDate dtFimCaucao;

    @Column(name = "SEQ_NIVEL")
    private Integer seqNivel;

    @Column(name = "FLG_CRITICA_NUMLOTE", length = 1)
    private String flgCriticaNumlote;

    @Column(name = "FLG_LIMITE_DIAS")
    private Integer flgLimiteDias;

    @Column(name = "FLG_PROCESSAMENTO_AUTOMATICO", length = 1)
    private String flgProcessamentoAutomatico;

    @Column(name = "FLG_PROCESSAMENTO_PARCIAL", length = 1)
    private String flgProcessamentoParcial;

    @Column(name = "FLG_SALDO_DEVEDOR", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String flgSaldoDevedor;

    @Column(name = "NUM_MINUTO_INI_LIB_RECARGA")
    private Integer numMinutoIniLibRecarga;

    @Column(name = "NUM_MINUTO_FIM_LIB_RECARGA")
    private Integer numMinutoFimLibRecarga;

    @Column(name = "FLG_EMITE_RECIBO_PEDIDO", columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String flgEmiteReciboPedido;

    @Column(name = "FLG_SUPERCANAL", length = 1, columnDefinition = "VARCHAR2(1) DEFAULT 'N'")
    private String flgSupercanal;

    @Column(name = "FLG_PAGTOFUTURO", length = 1, columnDefinition = "VARCHAR2(1) DEFAULT 'N'")
    private String flgPagtoFuturo;

    @Column(name = "COD_CLASSIFICACAO_PESSOA")
    private String codClassificacaoPessoa;

    @Column(name = "COD_ATIVIDADE")
    private String codAtividade;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}
