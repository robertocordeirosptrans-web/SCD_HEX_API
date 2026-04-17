package br.sptrans.scd.initializedcards.adapter.out.jpa.adapter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.initializedcards.adapter.out.jpa.mapper.RequestInitializedMapper;
import br.sptrans.scd.initializedcards.adapter.out.jpa.repository.RequestIniJpaRepository;
import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RICEntityJpa;
import br.sptrans.scd.initializedcards.application.port.out.RequestInitializedRepository;
import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RequestInitializedAdapter implements RequestInitializedRepository {

    private final RequestIniJpaRepository repository;
    private final RequestInitializedMapper mapper;

    @Override
    public RequestInitializedCards save(RequestInitializedCards entity) {
        RICEntityJpa ricEntity = mapper.toEntity(entity);
        RICEntityJpa saved = repository.save(ricEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public RequestInitializedCards findById(String codCanal, Long nrSolicitacao) {
        Optional<RICEntityJpa> result = repository.findByCodCanalAndNrSolicitacao(codCanal, nrSolicitacao);
        return result.map(mapper::toDomain).orElse(null);
    }

    @Override
    public Page<RequestInitializedCards> findAll(String codCanal, String codAdquirente, Pageable pageable) {
        Specification<RICEntityJpa> spec = Specification.where(null);
        if (codCanal != null && !codCanal.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("id").get("codCanal"), codCanal));
        }
        if (codAdquirente != null && !codAdquirente.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("codAdquirente"), codAdquirente));
        }
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public Long nextNrSolicitacao(String codTipoCanal, String codCanal) {
        Long next = repository.nextNrSolicitacao(codTipoCanal, codCanal);
        return next != null ? next : 1L;
    }
}
