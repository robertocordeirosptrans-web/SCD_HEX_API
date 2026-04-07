package br.sptrans.scd.product.adapter.out.persistence.entity;


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
    private BigDecimal valInicio;

    @Column(name = "VL_T_FINAL")
    private BigDecimal valFim;

    @Column(name = "VL_PERCENTUAL")
    private BigDecimal valPercentual;

    @Column(name = "DT_INICIO", nullable = false)
    private LocalDateTime dtInicio;

    @Column(name = "DT_FIM")
    private LocalDateTime dtFim;

   
    @Column(name = "DT_MODI", nullable = false)
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}
