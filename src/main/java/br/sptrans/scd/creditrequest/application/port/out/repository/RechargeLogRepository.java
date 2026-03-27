package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.Optional;

import br.sptrans.scd.creditrequest.domain.RechargeLog;

public interface RechargeLogRepository {

    /**
     * Persiste ou atualiza um log de recarga.
     */
    RechargeLog save(RechargeLog rechargeLog);

    /**
     * Busca um log de recarga pelo número lógico do cartão.
     */
    Optional<RechargeLog> findById(Integer seqRecarga);

    /**
     * Busca logs de recarga por número lógico do cartão.
     */
    Optional<RechargeLog> findByNumLogicoCartao(String numLogicoCartao);

}
