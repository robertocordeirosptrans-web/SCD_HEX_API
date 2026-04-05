package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;

public interface AgreementValidityPersistencePort {
    Optional<AgreementValidity> findById(AgreementValidityKey id);

    Optional<AgreementValidity> findByIdOtimized(String codCanal, String codProduto);

    List<AgreementValidity> findAll();

    List<AgreementValidity> findByCodCanal(String codCanal);

    List<AgreementValidity> findByCodProduto(String codProduto);

    AgreementValidity save(AgreementValidity entity);

    void deleteById(AgreementValidityKey id);

    boolean existsById(AgreementValidityKey id);
}
