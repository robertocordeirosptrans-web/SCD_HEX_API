package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestKey;

public interface HistCreditRequestRepository {

    /**
     * Persiste ou atualiza um histórico de solicitação de crédito.
     */
    HistCreditRequest save(HistCreditRequest histCreditRequest);

    /**
     * Persiste ou atualiza uma lista de históricos de solicitação de crédito.
     */
    List<HistCreditRequest> saveAll(List<HistCreditRequest> histCreditRequests);

    /**
     * Busca um histórico de solicitação pelo identificador composto.
     */
    Optional<HistCreditRequest> findById(HistCreditRequestKey id);

    /**
     * Lista todos os históricos por número de solicitação e canal.
     */
    List<HistCreditRequest> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal);

    /**
     * Retorna todos os registros de histórico de solicitações.
     */
    List<HistCreditRequest> findAll();

    /**
     * Verifica se existe um histórico para o identificador informado.
     */
    boolean existsById(HistCreditRequestKey id);

    /**
     * Retorna o total de registros de histórico.
     */
    long count();
}
