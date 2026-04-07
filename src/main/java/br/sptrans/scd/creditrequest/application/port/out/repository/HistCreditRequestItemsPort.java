package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItemsKey;

public interface HistCreditRequestItemsPort {
    /**
     * Persiste ou atualiza um histórico de item de solicitação de crédito.
     */
    HistCreditRequestItems save(HistCreditRequestItems histCreditRequestItems);

    /**
     * Persiste ou atualiza uma lista de históricos de itens.
     */
    List<HistCreditRequestItems> saveAll(List<HistCreditRequestItems> histCreditRequestItems);

    /**
     * Busca um histórico de item pelo identificador composto.
     */
    Optional<HistCreditRequestItems> findById(HistCreditRequestItemsKey id);

    /**
     * Lista todos os históricos de itens por número de solicitação e canal.
     */
    List<HistCreditRequestItems> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal);

    /**
     * Retorna todos os registros de histórico de itens.
     */
    List<HistCreditRequestItems> findAll();

    /**
     * Verifica se existe um histórico de item para o identificador informado.
     */
    boolean existsById(HistCreditRequestItemsKey id);

    /**
     * Retorna o total de registros de histórico de itens.
     */
    long count();

    /**
     * Obtém o maior número de sequência de histórico para um dado item.
     */
    Long findMaxSeqHistSdis(Long numSolicitacao,
            Long numSolicitacaoItem,
            String codCanal);

    /**
     * Retorna o histórico mais recente de um item de solicitação.
     */
    List<HistCreditRequestItems> findLatestByItem(Long numSolicitacao,
            Long numSolicitacaoItem,
            String codCanal);
}
