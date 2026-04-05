package br.sptrans.scd.initializedcards.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.initializedcards.adapter.port.out.jpa.mapper.TbLotSCDMapper;
import br.sptrans.scd.initializedcards.adapter.port.out.jpa.repository.TbLotJpaRepository;
import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.TbLotSCDEntityJpa;
import br.sptrans.scd.initializedcards.application.port.out.TbLotRepository;
import br.sptrans.scd.initializedcards.domain.TbLotSCD;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class TbLotAdapter implements TbLotRepository {

    private final TbLotJpaRepository repository;
    private final TbLotSCDMapper mapper;


    @Override
    public TbLotSCD findById(Long numLote) {
        Optional<TbLotSCDEntityJpa> result = repository.findById(numLote);
        return result.map(mapper::toDomain).orElse(null);
    }

    @Override
    public TbLotSCD findAll() {
        List<TbLotSCDEntityJpa> entities = repository.findAll();
        // Retornar null se não houver entidades, ou o primeiro, ou adaptar para retornar lista se interface mudar
        if (entities.isEmpty()) {
            return null;
        }
        // Exemplo: retorna o primeiro lote (ajuste conforme a interface TbLotRepository)
        return mapper.toDomain(entities.get(0));
        // Se TbLotRepository mudar para List<TbLotSCD>, pode-se retornar:
        // return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
    }

}
