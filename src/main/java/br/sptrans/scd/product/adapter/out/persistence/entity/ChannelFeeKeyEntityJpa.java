package br.sptrans.scd.product.adapter.out.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChannelFeeKeyEntityJpa implements Serializable {

    @Column(name = "COD_CANAL", nullable=false)
    private String codCanal;

    @Column(name = "COD_PRODUTO" , nullable = false)
    private String codProduto;
}
