package br.sptrans.scd.channel.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CANAIS_PRODUTOS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductChannelEntityJpa {

    @EmbeddedId
    private ProductChannelKeyEntityJpa id;

    @Column(name = "QTD_LIMITE_COMERCIALIZACAO")
    private Integer qtdLimiteComercializacao;

    @Column(name = "QTD_MINIMA_ESTOQUE")
    private Integer qtdMinimaEstoque;

    @Column(name = "QTD_MAXIMA_ESTOQUE")
    private Integer qtdMaximaEstoque;

    @Column(name = "QTD_MINIMA_RESSUPRIMENTO")
    private Integer qtdMinimaRessuprimento;

    @Column(name = "QTD_MAXIMA_RESSUPRIMENTO")
    private Integer qtdMaximaRessuprimento;

    @Column(name = "COD_ORGAO_EMISSOR")
    private Integer codOrgaoEmissor;

    @Column(name = "VL_FACE")
    private Integer vlFace;

    @Column(name = "ST_CANAIS_PRODUTOS", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "COD_CONVENIO")
    private Integer codConvenio;

    @Column(name = "TIPO_OPER_HM")
    private Integer codTipoOperHM;

    @Column(name = "FLG_CARAC", length = 1)
    private String flgCarac;

    @Column(name = "ID_USUARIO_CADASTRO")
    private Long idUsuarioCadastro;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}
