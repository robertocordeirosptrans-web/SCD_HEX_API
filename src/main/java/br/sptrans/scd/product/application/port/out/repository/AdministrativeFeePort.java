package br.sptrans.scd.product.application.port.out.repository;

import java.util.Optional;

import br.sptrans.scd.product.domain.AdministrativeFee;

public interface AdministrativeFeePort {
    Optional<AdministrativeFee> findAdmById(Long codTaxaAdm);
    AdministrativeFee save(AdministrativeFee taxasAdm);
}
