package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.mapper.TechnologyMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.TechnologyJpaRepository;
import br.sptrans.scd.product.application.port.out.repository.TechnologyPort;
import br.sptrans.scd.product.domain.Technology;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TechnologyAdapterJpa implements TechnologyPort {

    private final TechnologyJpaRepository repository;
    private final TechnologyMapper technologyMapper;

    @Override
    public Optional<Technology> findById(String codTecnologia) {
        return repository.findById(codTecnologia)
                .map(technologyMapper::toDomain);
    }

    @Override
    public boolean existsById(String codTecnologia) {
        return repository.existsById(codTecnologia);
    }

    @Override
    public List<Technology> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(technologyMapper::toDomain)
                    .filter(t -> codStatus.equals(t.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(technologyMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Technology> findAll(String codStatus, Pageable pageable) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findByCodStatus(codStatus, pageable)
                    .map(technologyMapper::toDomain);
        }
        return repository.findAll(pageable)
                .map(technologyMapper::toDomain);
    }

    @Override
    public Technology save(Technology technology) {
        var entity = technologyMapper.toEntity(technology);
        var saved = repository.save(entity);
        return technologyMapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codTecnologia, String codStatus, Long idUsuario) {
        repository.findById(codTecnologia).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            if (idUsuario != null) {
                UserEntityJpa userRef = new UserEntityJpa();
                userRef.setIdUsuario(idUsuario);
                entity.setUsuarioManutencao(userRef);
            }
            repository.save(entity);
        });
    }

    @Override
    public void deleteById(String codTecnologia) {
        repository.deleteById(codTecnologia);
    }

    @Override
    public Long findMaxNumericCode() {
        return repository.findMaxNumericCode();
    }
}
