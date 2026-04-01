package br.sptrans.scd.product.adapter.port.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TAXAS_DESTINO", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DestinyFeeEntityJpa {

    @Id
    @Column(name = "ID_TAXA", nullable = false)
    private Long codTaxaDes;

    @Column(name = "COD_CANALDESTINO", length = 20)
    private String codCanalDestino;
}
