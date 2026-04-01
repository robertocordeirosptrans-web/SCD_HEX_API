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
public class RechargeLimitKeyEntityJpa implements Serializable {

    @Column(name = "COD_CANAL")
    private String codCanal;

    @Column(name = "COD_PRODUTO")
    private String codProduto;
}
