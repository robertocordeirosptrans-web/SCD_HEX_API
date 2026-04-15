package br.sptrans.scd.auth.adapter.out.jpa;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.jpa.mapper.FunctionalityJpaMapper;
import br.sptrans.scd.auth.adapter.out.jpa.repository.FunctionalityJpaRepository;
import br.sptrans.scd.auth.application.port.out.FunctionalityPort;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FunctionalityAdapter implements FunctionalityPort {

    private final FunctionalityJpaRepository repository;

    @Override
    public Optional<Functionality> findById(FunctionalityKey id) {
        return repository.findById(FunctionalityJpaMapper.toEntityKey(id))
                .map(FunctionalityJpaMapper::toDomain);
    }

    @Override
    public Functionality save(Functionality functionality) {
        var entity = FunctionalityJpaMapper.toEntity(functionality);
        var saved = repository.save(entity);
        return FunctionalityJpaMapper.toDomain(saved);
    }

    @Override
    public void delete(FunctionalityKey id) {
        repository.deleteById(FunctionalityJpaMapper.toEntityKey(id));
    }

    @Override
    public void update(Functionality functionality) {
        var entity = FunctionalityJpaMapper.toEntity(functionality);
        repository.save(entity);
    }

    @Override
    public Page<Functionality> findAll(int page, int size) {
        var pageResult = repository.findAll(PageRequest.of(page, size));
        return pageResult.map(FunctionalityJpaMapper::toDomain);
    }
}
