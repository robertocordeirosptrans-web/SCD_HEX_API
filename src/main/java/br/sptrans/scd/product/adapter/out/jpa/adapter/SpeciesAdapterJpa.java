package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.application.port.out.SpeciesRepository;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.product.adapter.out.jpa.repository.SpeciesJpaRepository;
import br.sptrans.scd.product.adapter.out.jpa.mapper.SpeciesMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SpeciesAdapterJpa implements SpeciesRepository {

    private final SpeciesJpaRepository repository;

    @Override
    public Optional<Species> findById(String codEspecie) {
        return repository.findById(codEspecie)
                .map(SpeciesMapper::toDomain);
    }

    @Override
    public boolean existsById(String codEspecie) {
        return repository.existsById(codEspecie);
    }

    @Override
    public List<Species> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(SpeciesMapper::toDomain)
                    .filter(s -> codStatus.equals(s.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(SpeciesMapper::toDomain)
                .toList();
    }

    @Override
    public Species save(Species species) {
        // Mapper precisa ser implementado corretamente
        var entity = SpeciesMapper.toEntity(species);
        var saved = repository.save(entity);
        return SpeciesMapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codEspecie, String codStatus, Long idUsuario) {
        repository.findById(codEspecie).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            // Se existir campo de usuário, implemente aqui
            repository.save(entity);
        });
    }

    @Override
    public void deleteById(String codEspecie) {
        repository.deleteById(codEspecie);
    }



}
