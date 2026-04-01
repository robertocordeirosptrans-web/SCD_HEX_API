package br.sptrans.scd.channel.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MarketingDistribuitionChannelKey implements Serializable {

    private String codCanalComercializacao;

    private String codCanalDistribuicao;
}
