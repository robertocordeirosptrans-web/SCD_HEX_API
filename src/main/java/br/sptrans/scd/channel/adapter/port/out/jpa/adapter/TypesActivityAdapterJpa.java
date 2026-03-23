package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.channel.adapter.port.out.jpa.entity.TypesActivityEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.TypesActivityJpaRepository;
import br.sptrans.scd.channel.application.port.out.TypesActivityRepository;
import br.sptrans.scd.channel.domain.TypesActivity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class TypesActivityAdapterJpa implements TypesActivityRepository {

    private final TypesActivityJpaRepository typesActivityJpaRepository;


    @Override
    public Optional<TypesActivity> findById(String codAtividade) {
        return typesActivityJpaRepository.findById(codAtividade)
                .map(this::toDomain);
    }

    @Override
    public boolean existsById(String codAtividade) {
        return typesActivityJpaRepository.existsById(codAtividade);
    }

    @Override
    public List<TypesActivity> findAll(String codStatus) {
        return typesActivityJpaRepository.findAllByCodStatus(
                (codStatus != null && !codStatus.isBlank()) ? codStatus : null)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public TypesActivity save(TypesActivity typesActivity) {
        boolean exists = existsById(typesActivity.getCodAtividade());
        TypesActivityEntityJpa entity = toEntity(typesActivity);
        if (exists) {
            typesActivityJpaRepository.updateDescricao(entity.getCodAtividade(), entity.getDesAtividade());
        } else {
            entity.setCodStatus("A");
            entity.setDtCadastro(java.time.LocalDate.now().toString());
            entity.setDtManutencao(java.time.LocalDate.now().toString());
            typesActivityJpaRepository.save(entity);
        }
        return findById(typesActivity.getCodAtividade()).orElseThrow();
    }

    @Override
    public void updateStatus(String codAtividade, String codStatus) {
        typesActivityJpaRepository.updateStatus(codAtividade, codStatus);
    }

    @Override
    public void deleteById(String codAtividade) {
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
}
