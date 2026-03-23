package br.sptrans.scd.product.adapter.out.jpa.entity;

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
@Table(name = "TARIFAS", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FareEntityJpa {

    @Id
    @Column(name = "COD_TARIFA", nullable = false, length = 20)
    private String codTarifa;

    @Column(name = "COD_PRODUTO", nullable = false, length = 20)
    private String codProduto;

    @Column(name = "DT_VIGENCIA_INI", length = 20)
    private LocalDateTime dtVigenciaIni;

    @Column(name = "DT_VIGENCIA_FIM", length = 20)
    private LocalDateTime dtVigenciaFim;

    @Column(name = "DES_TARIFA", length = 60)
    private String desFamilia;

    
}
