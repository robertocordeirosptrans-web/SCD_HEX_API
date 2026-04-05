package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.AgreementValidityMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.AgreementValidityJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.AgreementValidityKeyEntityJpa;
import br.sptrans.scd.channel.application.port.out.AgreementValidityRepository;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AgreementValidityAdapterJpa implements AgreementValidityRepository {

    private final AgreementValidityJpaRepository jpaRepository;

    @Override
    public Optional<AgreementValidity> findById(AgreementValidityKey id) {
        if (id == null) {
            return Optional.empty();
        }
        AgreementValidityKeyEntityJpa entityKey = new AgreementValidityKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
        return jpaRepository.findById(entityKey)
                .map(AgreementValidityMapper::toDomain);
    }

    @Override
    public Optional<AgreementValidity> findByIdOtimized(String codCanal, String codProduto) {
        return jpaRepository.findByCodCanalAndCodProduto(codCanal, codProduto)
                .map(AgreementValidityMapper::toDomain);
    }

    @Override
    public List<AgreementValidity> findAll() {
        return jpaRepository.findAllAgreementValidity().stream()
                .map(AgreementValidityMapper::toDomain)
                .toList();
    }

    @Override
    public List<AgreementValidity> findByCodCanal(String codCanal) {
        return jpaRepository.findAllAgreementValidity().stream()
                .filter(e -> e.getId().getCodCanal().equals(codCanal))
                .map(AgreementValidityMapper::toDomain)
                .toList();
    }

    @Override
    public List<AgreementValidity> findByCodProduto(String codProduto) {
        return jpaRepository.findAllAgreementValidity().stream()
                .filter(e -> e.getId().getCodProduto().equals(codProduto))
                .map(AgreementValidityMapper::toDomain)
                .toList();
    }

    @Override
    public AgreementValidity save(AgreementValidity entity) {
        var entityJpa = AgreementValidityMapper.toEntity(entity);
        var saved = jpaRepository.save(entityJpa);
        return AgreementValidityMapper.toDomain(saved);
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
