package br.sptrans.scd.initializedcards.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.initializedcards.adapter.out.jpa.mapper.TbLotSCDMapper;
import br.sptrans.scd.initializedcards.adapter.out.jpa.repository.TbLotJpaRepository;
import br.sptrans.scd.initializedcards.application.port.out.TbLotRepository;
import br.sptrans.scd.initializedcards.domain.TbLotSCD;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TbLotAdapter implements TbLotRepository {

    private final TbLotJpaRepository repository;
    private final TbLotSCDMapper mapper;

    @Override
    public TbLotSCD findById(Long idLote) {
        Optional<br.sptrans.scd.initializedcards.adapter.out.persistence.entity.TbLotSCDEntityJpa> result =
                repository.findById(idLote);
        return result.map(mapper::toDomain).orElse(null);
    }

    @Override
    public List<TbLotSCD> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TbLotSCD> findAllByIds(List<Long> ids) {
        return repository.findAllById(ids)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
