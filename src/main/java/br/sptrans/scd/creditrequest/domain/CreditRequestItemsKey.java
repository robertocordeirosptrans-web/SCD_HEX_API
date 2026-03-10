package br.sptrans.scd.creditrequest.domain;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class CreditRequestItemsKey implements Serializable {

    private Long numSolicitacao;

    private Long numSolicitacaoItem;

    private String codCanal;
}
