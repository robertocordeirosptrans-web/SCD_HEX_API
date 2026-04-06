package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.TypesActivityMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.TypesActivityJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.TypesActivityEntityJpa;
import br.sptrans.scd.channel.application.port.out.TypesActivityPersistencePort;
import br.sptrans.scd.channel.domain.TypesActivity;
import lombok.RequiredArgsConstructor;

@Repository
@Transactional
@RequiredArgsConstructor
public class TypesActivityAdapterJpa implements TypesActivityPersistencePort {

    private static final String STATUS_INACTIVE = "I";

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
        String normalizedStatus = (codStatus != null && !codStatus.isBlank()) ? codStatus : null;
        return repository.findAllByCodStatus(normalizedStatus)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public TypesActivity save(TypesActivity typesActivity) {
        LocalDateTime now = LocalDateTime.now();

        if (repository.existsById(typesActivity.getCodAtividade())) {
            TypesActivityEntityJpa existing = repository.findById(typesActivity.getCodAtividade())
                    .orElseThrow();
            existing.setDesAtividade(typesActivity.getDesAtividade());
            existing.setDtManutencao(now);
            return mapper.toDomain(repository.save(existing));
        }

        TypesActivityEntityJpa entity = mapper.toEntity(typesActivity);
        if (entity.getCodStatus() == null || entity.getCodStatus().isBlank()) {
            entity.setCodStatus(STATUS_INACTIVE);
        }
        entity.setDtCadastro(now);
        entity.setDtManutencao(now);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void updateStatus(String codAtividade, String codStatus) {
        repository.updateStatus(codAtividade, codStatus);
    }

    @Override
    public void deleteById(String codAtividade) {
        repository.deleteById(codAtividade);
    }



}
