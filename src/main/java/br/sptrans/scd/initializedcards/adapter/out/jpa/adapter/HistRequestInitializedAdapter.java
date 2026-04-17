package br.sptrans.scd.initializedcards.adapter.out.jpa.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.initializedcards.adapter.out.jpa.mapper.HistRequestInitializedMapper;
import br.sptrans.scd.initializedcards.adapter.out.jpa.repository.HistRequestIniJpaRepository;
import br.sptrans.scd.initializedcards.application.port.out.HistRequestInitializedRepository;
import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HistRequestInitializedAdapter implements HistRequestInitializedRepository {

    private final HistRequestIniJpaRepository repository;
    private final HistRequestInitializedMapper mapper;

    @Override
    public HistRequestInitializedCards save(HistRequestInitializedCards entity) {
        var jpaEntity = mapper.toEntity(entity);
        var saved = repository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<HistRequestInitializedCards> findAllHist(String codCanal, Long nrSolicitacao) {
        return repository.findAllHistByCodCanalAndNrSolicitacao(codCanal, nrSolicitacao)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Long nextSeqHist(String codCanal, Long nrSolicitacao) {
        Long next = repository.nextSeqHist(codCanal, nrSolicitacao);
        return next != null ? next : 1L;
    }
}
