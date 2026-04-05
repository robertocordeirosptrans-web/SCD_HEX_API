package br.sptrans.scd.initializedcards.adapter.port.out.jpa.adapter;

import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import br.sptrans.scd.initializedcards.application.port.out.HistRequestInitializedRepository;
import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.repository.HistRequestIniJpaRepository;
import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.HistRICEntityJpa;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.mapper.HistRequestInitializedMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HistRequestInitializedAdapter implements HistRequestInitializedRepository {

    private final HistRequestIniJpaRepository repository;
    private final HistRequestInitializedMapper mapper;

    @Override
    public HistRequestInitializedCards save(HistRequestInitializedCards entity) {
        HistRICEntityJpa jpaEntity = mapper.toEntity(entity);
        repository.save(jpaEntity);
        return entity;
    }

    @Override
    public HistRequestInitializedCards findByHistId(String codCanal, Long nrSolicitacao, String seqHistSolicCartaoIni) {
        // seqHistSolicCartaoIni convertido para Long
        Optional<HistRICEntityJpa> result = repository.findByHistId(codCanal, nrSolicitacao, Long.valueOf(seqHistSolicCartaoIni));
        return result.map(mapper::toDomain).orElse(null);
    }

    @Override
    public HistRequestInitializedCards findAllHist(String codCanal, Long nrSolicitacao, String codAdquirente) {
        Optional<HistRICEntityJpa> result = repository.findByAllHist(codCanal, nrSolicitacao, codAdquirente);
        return result.map(mapper::toDomain).orElse(null);
    }
}
