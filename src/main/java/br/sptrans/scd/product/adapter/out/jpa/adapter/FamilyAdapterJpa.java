package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.product.adapter.out.jpa.mapper.FamilyMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.FamilyJpaRepository;
import br.sptrans.scd.product.adapter.out.persistence.entity.FamilyEntityJpa;
import br.sptrans.scd.product.application.port.out.repository.FamilyPort;
import br.sptrans.scd.product.domain.Family;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor

public class FamilyAdapterJpa implements FamilyPort {

    private final FamilyJpaRepository repository;
    private final UserPersistencePort userRepository;

    @Override

    public Optional<Family> findById(String codFamilia) {
        return repository.findById(codFamilia)
                .map(entity -> FamilyMapper.toDomain(entity, userRepository));
    }

    @Override

    public boolean existsById(String codFamilia) {
        return repository.existsById(codFamilia);
    }


    @Override
    public Page<Family> findAll(String codStatus, Pageable pageable) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findByCodStatus(codStatus, pageable)
                    .map(entity -> FamilyMapper.toDomain(entity, userRepository));
        }
        return repository.findAll(pageable)
                .map(entity -> FamilyMapper.toDomain(entity, userRepository));
    }

    @Override

    public Family save(Family family) {
        FamilyEntityJpa entity = FamilyMapper.toEntity(family);
        FamilyEntityJpa saved = repository.save(entity);
        return FamilyMapper.toDomain(saved, userRepository);
    }

    @Override
    public void updateStatus(String codFamilia, String codStatus, Long idUsuario) {
        repository.updateStatus(codFamilia, codStatus, idUsuario);
    }

    @Override
    public void deleteById(String codFamilia) {
        repository.deleteById(codFamilia);
    }

}
