package br.sptrans.scd.product.adapter.out.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "TaxasScanalJpa")
@Table(name = "TAXA_SCANAL", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelFeeEntityJpa {

    @EmbeddedId
    private ChannelFeeKeyEntityJpa id;

    @Column(name = "VL_T_INICIO")
    private BigDecimal vltInicio;

    @Column(name = "VL_T_FINAL")
    private BigDecimal vltFinal;

    @Column(name = "VL_PERCENTUAL")
    private BigDecimal vlPercentual;

    @Column(name = "DT_INICIO")
    private LocalDateTime dtInicio;

    @Column(name = "DT_FIM")
    private LocalDateTime dtFinal;

    @Column(name = "DT_MODI")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}
