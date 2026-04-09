package br.sptrans.scd.creditrequest.application.port.in;

import java.util.List;
import java.util.Map;

import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;

public interface HistCreditRequestManagementUseCase {

    List<HistCreditRequestItems> findItemStatusHistory(Long numSolicitacao, Long numSolicitacaoItem, String codCanal);

    List<HistCreditRequest> findRequestStatusHistory(Long numSolicitacao, String codCanal);

    void saveItemStatusHistoryBatch(List<CreditRequestItems> itens, String origemTransicao);

    /**
     * Salva histórico em lote com descrições de ocorrência individuais por item.
     * A chave do mapa é {@code numSolicitacaoItem}; ausência de chave significa sem descrição.
     */
    void saveItemStatusHistoryBatch(List<CreditRequestItems> itens, String origemTransicao,
            Map<Long, String> descricaoPorItem);

    void saveRequestStatusHistory(CreditRequest request, Long numSolicitacao, String codCanal, String origemTransicao);
}
