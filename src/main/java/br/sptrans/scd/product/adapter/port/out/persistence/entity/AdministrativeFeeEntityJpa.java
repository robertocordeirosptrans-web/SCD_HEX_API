package br.sptrans.scd.product.adapter.port.out.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TAXAS_ADMINISTRATIVA", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdministrativeFeeEntityJpa {

    @Id
    @Column(name = "ID_TAXA", nullable = false)
    private Long codTaxaAdm;

    @Column(name = "REC_INICIAL")
    private Byte recInicial;

    @Column(name = "REC_FINAL")
    private Byte recFinal;

    @Column(name = "VAL_FIXO")
    private BigDecimal valFixo;

    @Column(name = "VAL_PERCENTUAL")
    private BigDecimal valPercentual;
}
