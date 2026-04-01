package br.sptrans.scd.initializedcards.adapter.port.out.jpa.adapter;


import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.initializedcards.adapter.port.out.jpa.mapper.RequestInitializedMapper;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.repository.RequestIniJpaRepository;
import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.RICEntityJpa;
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
	public RequestInitializedCards findAll(String codCanal, Long nrSolicitacao, String codAdquirente) {
		Optional<RICEntityJpa> result = repository.findByCodCanalAndNrSolicitacaoAndCodAdquirente(codCanal, nrSolicitacao, codAdquirente);
		return result.map(mapper::toDomain).orElse(null);
	}
}
