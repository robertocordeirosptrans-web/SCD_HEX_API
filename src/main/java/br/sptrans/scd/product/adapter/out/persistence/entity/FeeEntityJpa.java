package br.sptrans.scd.product.adapter.out.persistence.entity;


import java.time.LocalDateTime;

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
@Table(name = "TAXAS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeEntityJpa {

    @Id
    @NotNull
    @Column(name = "ID_TAXA", nullable = false, length = 20)
    private Long codTaxa;

    @NotNull
    @Column(name = "COD_CANAL", nullable = false, length = 20)
    private String codCanal;

    @NotNull
    @Column(name = "COD_PRODUTO", nullable = false, length = 20)
    private String codProduto;

    @NotNull
    @Column(name = "DT_INICIAL")
    private LocalDateTime dtInicio;

    @Column(name = "DSC_TAXA", length = 20)
    private String desTaxa;

    @Column(name = "DT_FINAL")
    private LocalDateTime dtFim;

}
