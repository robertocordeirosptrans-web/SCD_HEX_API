package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;
import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.adapter.port.out.jpa.repository.TypesActivityJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.TypesActivityEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.TypesActivityMapper;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.application.port.out.TypesActivityRepository;
import br.sptrans.scd.channel.domain.TypesActivity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class TypesActivityAdapterJpa implements TypesActivityRepository {

    private final TypesActivityJpaRepository repository;
    private final TypesActivityMapper mapper;

    @Override
    public Optional<TypesActivity> findById(String codAtividade) {
        return repository.findById(codAtividade)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(String codAtividade) {
        return repository.existsById(codAtividade);
    }

    @Override
    public List<TypesActivity> findAll(String codStatus) {
        List<TypesActivityEntityJpa> entities;
        if (codStatus != null && !codStatus.isBlank()) {
            entities = repository.findAll((root, query, cb) ->
                    cb.equal(root.get("codStatus"), codStatus));
        } else {
            entities = repository.findAll();
        }
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public TypesActivity save(TypesActivity typesActivity) {
        TypesActivityEntityJpa entity = mapper.toEntity(typesActivity);
        TypesActivityEntityJpa saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codAtividade, String codStatus) {
        repository.findById(codAtividade).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            // Atualize dtManutencao se necessário
            repository.save(entity);
        });
    }

    @Override
    public void deleteById(String codAtividade) {
        repository.deleteById(codAtividade);
    }

}
