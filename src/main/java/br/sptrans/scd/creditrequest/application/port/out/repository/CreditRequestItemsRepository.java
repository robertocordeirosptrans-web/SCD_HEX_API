package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;

public interface CreditRequestItemsRepository {

    /**
     * Busca o primeiro item de recarga com situação e data de pagamento econômica no intervalo informado.
     */
    List<CreditRequestItemsEJpa> findFirstBySituacaoAndDtPagtoEconomicaBetween(String codSituacao,Timestamp dtInicio,Timestamp dtFim, Integer limit);

    CreditRequestItems save(CreditRequestItems items);

    Optional<CreditRequestItems> findById(CreditRequestItemsKey id);

    /**
     * Busca todos os números de solicitação de item para uma solicitação, canal e lote.
     */
    List<Long> findNumSolicitacaoItemsBySolicitacaoCanalLote(Long numSolicitacao, String codCanal, String numLote);

    List<CreditRequestItemsEJpa> findProcessRechargeService(Long numSolicitacao, String codCanal, String numLote);
}
