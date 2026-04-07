package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

// ...
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestKeyEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.HistCreditRequestMapper;
import br.sptrans.scd.creditrequest.adapter.out.jpa.repository.HistCreditJpaRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.HistCreditRequestPort;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestKey;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class HistCreditRequestAdapterJpa implements HistCreditRequestPort {

    private final HistCreditJpaRepository jpaRepository;
    private final HistCreditRequestMapper mapper;


    @Override
    public HistCreditRequest save(HistCreditRequest h) {
        HistCreditRequestEJpa entity = mapper.toEntity(h);
        HistCreditRequestEJpa saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }


    @Override
    public List<HistCreditRequest> saveAll(List<HistCreditRequest> items) {
        List<HistCreditRequestEJpa> entities = items.stream().map(mapper::toEntity).toList();
        List<HistCreditRequestEJpa> saved = jpaRepository.saveAll(entities);
        return saved.stream().map(mapper::toDomain).toList();
    }


    @Override
    public Optional<HistCreditRequest> findById(HistCreditRequestKey id) {
        HistCreditRequestKeyEJpa keyEntity = mapper.toEntityKey(id);
        return jpaRepository.findById(keyEntity).map(mapper::toDomain);
    }


    @Override
    public List<HistCreditRequest> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal) {
        return jpaRepository.findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal)
                .stream().map(mapper::toDomain).toList();
    }


    @Override
    public List<HistCreditRequest> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }


    @Override
    public boolean existsById(HistCreditRequestKey id) {
        HistCreditRequestKeyEJpa keyEntity = mapper.toEntityKey(id);
        return jpaRepository.existsById(keyEntity);
    }


    @Override
    public long count() {
        return jpaRepository.count();
    }


    @Override
    public Long findMaxSeqHistSdis(Long numSolicitacao, String codCanal) {
        return jpaRepository.findMaxSeqHistSdis(numSolicitacao, codCanal);
    }


    @Override
    public List<HistCreditRequest> findLatestBySolicitacao(Long numSolicitacao, String codCanal) {
        return jpaRepository.findLatestBySolicitacao(numSolicitacao, codCanal)
                .stream().map(mapper::toDomain).toList();
    }

}
