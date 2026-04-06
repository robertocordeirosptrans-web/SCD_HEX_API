package br.sptrans.scd.channel.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@EqualsAndHashCode
public class AgreementValidityKey implements Serializable{

    private final String codCanal;

    private final String codProduto;

}
