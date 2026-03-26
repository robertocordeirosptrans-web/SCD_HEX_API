package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;

public interface CreditRequestItemsRepository {

    CreditRequestItems save(CreditRequestItems items);

    Optional<CreditRequestItems> findById(CreditRequestItemsKey id);

    /**
     * Busca todos os números de solicitação de item para uma solicitação, canal e lote.
     */
    List<Long> findNumSolicitacaoItemsBySolicitacaoCanalLote(Long numSolicitacao, String codCanal, String numLote);

    List<CreditRequestItemsEJpa> findProcessRechargeService(Long numSolicitacao, String codCanal, String numLote);
}
