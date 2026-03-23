package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.product.adapter.out.jpa.entity.FamilyEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.mapper.FamilyMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.FamilyJpaRepository;
import br.sptrans.scd.product.application.port.out.FamilyRepository;
import br.sptrans.scd.product.domain.Family;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class FamilyAdapterJpa implements FamilyRepository {

    private final FamilyJpaRepository repository;

    @Override

    public Optional<Family> findById(String codFamilia) {
        return repository.findById(codFamilia).map(FamilyMapper::toDomain);
    }

    @Override

    public boolean existsById(String codFamilia) {
        return repository.existsById(codFamilia);
    }

    @Override
    public List<Family> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findByCodStatusOrderByCodFamilia(codStatus).stream().map(FamilyMapper::toDomain).toList();
        }
        return repository.findAllByOrderByCodFamilia().stream().map(FamilyMapper::toDomain).toList();
    }

    @Override
    public Family save(Family family) {
        FamilyEntityJpa entity = FamilyMapper.toEntity(family);
        FamilyEntityJpa saved = repository.save(entity);
        return FamilyMapper.toDomain(saved);
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
