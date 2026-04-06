package br.sptrans.scd.product.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LNK_SCP_TB_TPCARTAO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardsTypeEntityJpa {

    @Id
    @Column(name = "NI_IDTPCARTAO", length = 10)
    private String codTipoCartao;

    @Column(name = "VC_DESC", length = 10)
    private String desTipoCartao;
}
