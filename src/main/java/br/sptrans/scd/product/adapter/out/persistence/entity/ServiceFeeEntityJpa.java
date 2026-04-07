package br.sptrans.scd.product.adapter.out.persistence.entity;


import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TAXAS_SERVICO", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFeeEntityJpa {

    @Id
    @NotNull
    @Column(name = "ID_TAXA", nullable = false)
    private Long codTaxaSrv;

    @NotNull
    @Column(name = "REC_INICIAL")
    private Byte recInicial;

    @NotNull
    @Column(name = "REC_FINAL")
    private Byte recFinal;

    @Column(name = "VAL_FIXO")
    private BigDecimal valFixo;

    @Column(name = "VAL_PERCENTUAL")
    private BigDecimal valPercentual;

    @NotNull
    @Column(name = "VAL_MINIMO")
    private BigDecimal valMinimo;
}
