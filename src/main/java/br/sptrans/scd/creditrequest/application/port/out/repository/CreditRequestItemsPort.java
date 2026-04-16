package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;

public interface CreditRequestItemsPort extends ReportCreditPort {
    /**
     * Busca os primeiros 100 itens de recarga com situação informada.
     */
    List<CreditRequestItems> searchForItensUnlocked(String codSituacao);

    CreditRequestItems save(CreditRequestItems items);

    Optional<CreditRequestItems> findById(CreditRequestItemsKey id);

    List<CreditRequestItems> findAllById(List<CreditRequestItemsKey> ids);

    List<CreditRequestItems> findAllBySolicitacao(Long numSolicitacao, String codCanal);

    /**
     * Busca todos os números de solicitação de item para uma solicitação, canal e lote.
     */
    List<Long> findNumSolicitacaoItemsBySolicitacaoCanalLote(Long numSolicitacao, String codCanal, String numLote);

    List<CreditRequestItems> findProcessRechargeService(Long numSolicitacao, String codCanal, String numLote);

    /**
     * Busca itens elegíveis para processamento de recarga com a situação informada.
     */
    List<CreditRequestItems> searchItemsToBeProcessed(String codSituacao);
}
