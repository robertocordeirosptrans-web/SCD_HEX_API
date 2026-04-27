package br.sptrans.scd.initializedcards.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.initializedcards.adapter.out.jpa.mapper.RequestLotMapper;
import br.sptrans.scd.initializedcards.adapter.out.jpa.repository.RequestLotJpaRepository;
import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RequestLotSCPEntityJpa;
import br.sptrans.scd.initializedcards.application.port.out.RequestLotSCPRepository;
import br.sptrans.scd.initializedcards.domain.RequestLotSCP;
import br.sptrans.scd.initializedcards.domain.RequestLotSCPKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RequestLotSCPAdapter implements RequestLotSCPRepository {

    private final RequestLotJpaRepository repository;
    private final RequestLotMapper mapper;

    @Override
    public RequestLotSCP save(RequestLotSCP entity) {
        RequestLotSCPEntityJpa ricEntity = mapper.toEntity(entity);
        RequestLotSCPEntityJpa saved = repository.save(ricEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public RequestLotSCP findById(RequestLotSCPKey id) {
        Optional<RequestLotSCPEntityJpa> result = repository.findById(mapper.toEntityKey(id));
        return result.map(mapper::toDomain).orElse(null);
    }

    @Override
    public List<RequestLotSCP> findAllBySolicitacao(String codCanal, Long nrSolicitacao) {
        return repository.findAllByCodCanalAndNrSolicitacao(codCanal, nrSolicitacao)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllBySolicitacao(String codCanal, Long nrSolicitacao) {
        repository.deleteAllByCodCanalAndNrSolicitacao(codCanal, nrSolicitacao);
    }
}
