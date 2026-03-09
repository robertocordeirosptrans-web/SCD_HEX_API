package br.sptrans.scd.channel.domain;

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
public class MarketingDistribuitionChannelKey {

    private String codCanalComercializacao;

    private String codCanalDistribuicao;
}
