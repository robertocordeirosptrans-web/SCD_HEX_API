package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.RechargeLog;

public interface RechargeLogRepository {

    /**
     * Persiste ou atualiza um log de recarga.
     */
    RechargeLog save(RechargeLog rechargeLog);

    /**
     * Persiste ou atualiza uma lista de logs de recarga.
     */
    List<RechargeLog> saveAll(List<RechargeLog> rechargeLogs);

    /**
     * Busca um log de recarga pelo número lógico do cartão.
     */
    Optional<RechargeLog> findById(String numLogicoCartao);

    /**
     * Busca logs de recarga por número lógico do cartão e sequência de recarga.
     */
    Optional<RechargeLog> findByNumLogicoCartaoAndSeqRecarga(String numLogicoCartao, Integer seqRecarga);

    /**
     * Retorna todos os logs de recarga.
     */
    List<RechargeLog> findAll();

    /**
     * Verifica se existe um log de recarga para o número lógico do cartão informado.
     */
    boolean existsById(String numLogicoCartao);

    /**
     * Retorna o total de logs de recarga.
     */
    long count();
}
