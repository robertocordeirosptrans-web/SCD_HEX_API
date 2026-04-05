package br.sptrans.scd.channel.adapter.port.out.persistence.entity;

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
public class ProductChannelKeyEntityJpa implements Serializable {

    @Column(name = "COD_CANAL", nullable = false, length = 20)
    private String codCanal;

    @Column(name = "COD_PRODUTO", nullable = false, length = 20)
    private String codProduto;
}
