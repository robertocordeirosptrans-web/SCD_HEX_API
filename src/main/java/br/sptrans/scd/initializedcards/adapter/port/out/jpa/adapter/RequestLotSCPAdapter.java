package br.sptrans.scd.initializedcards.adapter.port.out.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity.RequestLotSCPEntityJpa;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.entity.RequestLotSCPEntityJpaKey;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.mapper.RequestLotMapper;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.repository.RequestLotJpaRepository;
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
        return result.map(RequestLotMapper::toDomain).orElse(null);
    }


    
    @Override
    public RequestLotSCP findAll(String codCanal, Long nrSolicitacao, String codAdquirente) {
       
        return null;
    }



    // O método findAll customizado foi removido pois não existe no repository padrão
    // Caso precise de um findAll, utilize repository.findAll() e faça o mapeamento necessário


    @Override
    public RequestLotSCP delete(String codCanal, Long nrSolicitacao, Long numLote) {

        RequestLotSCPEntityJpaKey key = new RequestLotSCPEntityJpaKey();
        key.setCodCanal(codCanal);
        key.setNrSolicitacao(nrSolicitacao);
        key.setIdLote(numLote);
        // Os campos abaixo precisam ser preenchidos corretamente conforme sua regra de negócio
        key.setCodTipoCanal(null); // Ajuste conforme necessário
        key.setFlgFaseSolicitacao(null); // Ajuste conforme necessário

        Optional<RequestLotSCPEntityJpa> entityOpt = repository.findById(key);
        if (entityOpt.isPresent()) {
            repository.deleteById(key);
            return RequestLotMapper.toDomain(entityOpt.get());
        }
        return null;
    }
}
