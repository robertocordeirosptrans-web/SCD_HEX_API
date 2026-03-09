package br.sptrans.scd.product.application.port.out;

import java.util.List;

import br.sptrans.scd.product.domain.CardType;

/**
 * Output Port para operações de persistência de TipodeCartao.
 */
public interface CardTypeRepository {
    List<CardType> findAllViaDblink();
}
