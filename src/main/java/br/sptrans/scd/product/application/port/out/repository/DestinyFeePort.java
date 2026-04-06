package br.sptrans.scd.product.application.port.out.repository;

import java.util.Optional;

import br.sptrans.scd.product.domain.DestinyFee;

public interface DestinyFeePort {
    Optional<DestinyFee> findDesById(Long id);
    DestinyFee save(DestinyFee taxasScanal);
    boolean existsByDesId(DestinyFee id);
}
