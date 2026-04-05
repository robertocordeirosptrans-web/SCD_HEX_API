package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

<<<<<<< HEAD
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.TypesActivityJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.TypesActivityEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.TypesActivityMapper;
=======
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.adapter.port.out.jpa.repository.TypesActivityJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.TypesActivityEntityJpa;
import br.sptrans.scd.channel.application.port.out.TypesActivityRepository;
import br.sptrans.scd.channel.domain.TypesActivity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
<<<<<<< HEAD
@SuppressWarnings("unchecked")
public class TypesActivityAdapterJpa implements TypesActivityRepository {

    private final TypesActivityJpaRepository repository;
    private final TypesActivityMapper mapper;

    @Override
    public Optional<TypesActivity> findById(String codAtividade) {
        return repository.findById(codAtividade)
                .map(mapper::toDomain);
=======
@Transactional
public class TypesActivityAdapterJpa implements TypesActivityRepository {

    private final TypesActivityJpaRepository typesActivityJpaRepository;


    @Override
    public Optional<TypesActivity> findById(String codAtividade) {
        return typesActivityJpaRepository.findById(codAtividade)
                .map(this::toDomain);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public boolean existsById(String codAtividade) {
<<<<<<< HEAD
        return repository.existsById(codAtividade);
=======
        return typesActivityJpaRepository.existsById(codAtividade);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public List<TypesActivity> findAll(String codStatus) {
<<<<<<< HEAD
        List<TypesActivityEntityJpa> entities;
        if (codStatus != null && !codStatus.isBlank()) {
            entities = repository.findAll((root, query, cb) ->
                    cb.equal(root.get("codStatus"), codStatus));
        } else {
            entities = repository.findAll();
        }
        return entities.stream().map(mapper::toDomain).toList();
=======
        return typesActivityJpaRepository.findAllByCodStatus(
                (codStatus != null && !codStatus.isBlank()) ? codStatus : null)
                .stream().map(this::toDomain).toList();
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public TypesActivity save(TypesActivity typesActivity) {
<<<<<<< HEAD
        TypesActivityEntityJpa entity = mapper.toEntity(typesActivity);
        TypesActivityEntityJpa saved = repository.save(entity);
        return mapper.toDomain(saved);
=======
        boolean exists = existsById(typesActivity.getCodAtividade());
        TypesActivityEntityJpa entity = toEntity(typesActivity);
        if (exists) {
            typesActivityJpaRepository.updateDescricao(entity.getCodAtividade(), entity.getDesAtividade());
        } else {
            entity.setCodStatus("A");
            entity.setDtCadastro(LocalDateTime.now());
            entity.setDtManutencao(LocalDateTime.now());
            typesActivityJpaRepository.save(entity);
        }
        return findById(typesActivity.getCodAtividade()).orElseThrow();
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public void updateStatus(String codAtividade, String codStatus) {
<<<<<<< HEAD
        repository.findById(codAtividade).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            // Atualize dtManutencao se necessário
            repository.save(entity);
        });
=======
        typesActivityJpaRepository.updateStatus(codAtividade, codStatus);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public void deleteById(String codAtividade) {
<<<<<<< HEAD
        repository.deleteById(codAtividade);
    }

=======
        typesActivityJpaRepository.deleteById(codAtividade);
    }

    private TypesActivity toDomain(TypesActivityEntityJpa entity) {
        return new TypesActivity(
                entity.getCodAtividade(),
                entity.getDesAtividade(),
                entity.getCodStatus(),
                entity.getDtCadastro(),
                entity.getDtManutencao()
        );
    }

    private TypesActivityEntityJpa toEntity(TypesActivity domain) {
        return new TypesActivityEntityJpa(
                domain.getCodAtividade(),
                domain.getDesAtividade(),
                domain.getCodStatus(),
                domain.getDtCadastro(),
                domain.getDtManutencao()
        );
    }
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
}
