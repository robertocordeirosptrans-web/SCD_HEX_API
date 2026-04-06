package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.AgreementValidityMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.AgreementValidityJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.AgreementValidityKeyEntityJpa;
import br.sptrans.scd.channel.application.port.out.AgreementValidityPersistencePort;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AgreementValidityAdapterJpa implements AgreementValidityPersistencePort {

    private final AgreementValidityJpaRepository jpaRepository;
    private final AgreementValidityMapper agreementValidityMapper;

    @Override
    public Optional<AgreementValidity> findById(AgreementValidityKey id) {
        if (id == null) {
            return Optional.empty();
        }
        AgreementValidityKeyEntityJpa entityKey = new AgreementValidityKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
        return jpaRepository.findById(entityKey)
                .map(agreementValidityMapper::toDomain);
    }

    @Override
    public Optional<AgreementValidity> findByIdOtimized(String codCanal, String codProduto) {
        return jpaRepository.findByCodCanalAndCodProduto(codCanal, codProduto)
                .map(agreementValidityMapper::toDomain);
    }

    @Override
    public Page<AgreementValidity> findAll(Pageable pageable) {
        return jpaRepository.findAllAgreementValidity(pageable)
                .map(agreementValidityMapper::toDomain);
    }

    @Override
    public Page<AgreementValidity> findByCodCanal(String codCanal, Pageable pageable) {
        return jpaRepository.findByCodCanal(codCanal, pageable)
                .map(agreementValidityMapper::toDomain);
    }

    @Override
    public Page<AgreementValidity> findByCodProduto(String codProduto, Pageable pageable) {
        return jpaRepository.findByCodProduto(codProduto, pageable)
                .map(agreementValidityMapper::toDomain);
    }

    @Override
    public AgreementValidity save(AgreementValidity entity) {
        var entityJpa = agreementValidityMapper.toEntity(entity);
        var saved = jpaRepository.save(entityJpa);
        return agreementValidityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(AgreementValidityKey id) {
        jpaRepository.deleteById(new AgreementValidityKeyEntityJpa(id.getCodCanal(), id.getCodProduto()));
    }

    @Override
    public boolean existsById(AgreementValidityKey id) {
        return jpaRepository.existsByCodCanalAndCodProduto(id.getCodCanal(), id.getCodProduto());
    }

}
