package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.mapper.SpeciesMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.SpeciesJpaRepository;
import br.sptrans.scd.product.application.port.out.repository.SpeciesPort;
import br.sptrans.scd.product.domain.Species;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SpeciesAdapterJpa implements SpeciesPort {

    private final SpeciesJpaRepository repository;
    private final SpeciesMapper speciesMapper;

    @Override
    public Optional<Species> findById(String codEspecie) {
        return repository.findById(codEspecie)
                .map(speciesMapper::toDomain);
    }

    @Override
    public boolean existsById(String codEspecie) {
        return repository.existsById(codEspecie);
    }

    @Override
    public List<Species> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(speciesMapper::toDomain)
                    .filter(s -> codStatus.equals(s.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(speciesMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Species> findAll(String codStatus, Pageable pageable) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findByCodStatus(codStatus, pageable)
                    .map(speciesMapper::toDomain);
        }
        return repository.findAll(pageable)
                .map(speciesMapper::toDomain);
    }

    @Override
    public Species save(Species species) {
        var entity = speciesMapper.toEntity(species);
        var saved = repository.save(entity);
        return speciesMapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codEspecie, String codStatus, Long idUsuario) {
        repository.findById(codEspecie).ifPresent(entity -> {
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
    public void deleteById(String codEspecie) {
        repository.deleteById(codEspecie);
    }



}
