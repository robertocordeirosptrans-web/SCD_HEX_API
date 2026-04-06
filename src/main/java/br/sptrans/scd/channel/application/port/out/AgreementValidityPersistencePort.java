package br.sptrans.scd.channel.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;

public interface AgreementValidityPersistencePort {
    Optional<AgreementValidity> findById(AgreementValidityKey id);



    Page<AgreementValidity> findAll(Pageable pageable);

    Page<AgreementValidity> findByCodCanal(String codCanal, Pageable pageable);

    Page<AgreementValidity> findByCodProduto(String codProduto, Pageable pageable);

    AgreementValidity save(AgreementValidity entity);

    void deleteById(AgreementValidityKey id);

    boolean existsById(AgreementValidityKey id);
}
