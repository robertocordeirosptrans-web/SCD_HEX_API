package br.sptrans.scd.product.application.port.out.repository;

import java.util.Optional;

import br.sptrans.scd.product.domain.DestinyFee;

public interface DestinyFeeRepository {


    Optional<DestinyFee> findDesById(Long id);

    /**
     * Salva ou atualiza uma taxa de sub-canal.
     *
     * @param taxasScanal Taxa de sub-canal a ser salva
     * @return Taxa de sub-canal salva
     */
    DestinyFee save(DestinyFee taxasScanal);

    /**
     * Verifica se uma taxa de sub-canal existe pelo ID composto.
     *
     * @param id ID composto
     * @return true se existe, false caso contrário
     */
    boolean existsByDesId(DestinyFee id);
}
