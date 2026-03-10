package br.sptrans.scd.channel.adapter.port.out.jpa.entity;

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
public class MarketingDistribuitionChannelKeyEntityJpa implements Serializable{

    @Column(name = "COD_CANAL_COMERC", length = 20)
    private String codCanalComercializacao;

    @Column(name = "COD_CANAL_DISTRIB", length = 20)
    private String codCanalDistribuicao;
}
