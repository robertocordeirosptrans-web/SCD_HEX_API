package br.sptrans.scd.channel.adapter.out.jpa.adapter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.channel.adapter.out.jpa.mapper.AgreementValidityMapper;
import br.sptrans.scd.channel.adapter.out.jpa.repository.AgreementValidityJpaRepository;
import br.sptrans.scd.channel.adapter.out.persistence.entity.AgreementValidityEntityJpa;
import br.sptrans.scd.channel.adapter.out.persistence.entity.AgreementValidityKeyEntityJpa;
import br.sptrans.scd.channel.application.port.out.AgreementValidityPersistencePort;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AgreementValidityAdapterJpa implements AgreementValidityPersistencePort {

    private final AgreementValidityJpaRepository jpaRepository;
    private final AgreementValidityMapper agreementValidityMapper;
    private final UserPersistencePort userRepository;

    private AgreementValidity toDomainWithUser(AgreementValidityEntityJpa entity) {
        AgreementValidity domain = agreementValidityMapper.toDomain(entity);
        if (entity.getIdUsuario() != null) {
            domain.setUsuario(userRepository.findById(entity.getIdUsuario()).orElse(null));
        }
        return domain;
    }

    @Override
    public Optional<AgreementValidity> findById(AgreementValidityKey id) {
        if (id == null) {
            return Optional.empty();
        }
        AgreementValidityKeyEntityJpa entityKey = new AgreementValidityKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
        return jpaRepository.findById(entityKey)
                .map(this::toDomainWithUser);
    }



    @Override
    public Page<AgreementValidity> findAll(Pageable pageable) {
        return jpaRepository.findAllAgreementValidity(pageable)
                .map(this::toDomainWithUser);
    }

    @Override
    public Page<AgreementValidity> findByCodCanal(String codCanal, Pageable pageable) {
        return jpaRepository.findByCodCanal(codCanal, pageable)
                .map(this::toDomainWithUser);
    }

    @Override
    public Page<AgreementValidity> findByCodProduto(String codProduto, Pageable pageable) {
        return jpaRepository.findByCodProduto(codProduto, pageable)
                .map(this::toDomainWithUser);
    }

    @Override
    public AgreementValidity save(AgreementValidity entity) {
        var entityJpa = agreementValidityMapper.toEntity(entity);
        var saved = jpaRepository.save(entityJpa);
        return toDomainWithUser(saved);
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
