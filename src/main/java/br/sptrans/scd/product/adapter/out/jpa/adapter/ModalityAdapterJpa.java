package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.mapper.ModalityMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.ModalityJpaRepository;
import br.sptrans.scd.product.application.port.out.repository.ModalityPort;
import br.sptrans.scd.product.domain.Modality;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ModalityAdapterJpa implements ModalityPort {

    private final ModalityJpaRepository repository;
    private final ModalityMapper modalityMapper;

    @Override
    public Optional<Modality> findById(String codModalidade) {
        return repository.findById(codModalidade)
                .map(modalityMapper::toDomain);
    }

    @Override
    public boolean existsById(String codModalidade) {
        return repository.existsById(codModalidade);
    }

    @Override
    public List<Modality> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(modalityMapper::toDomain)
                    .filter(m -> codStatus.equals(m.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(modalityMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Modality> findAll(String codStatus, Pageable pageable) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findByCodStatus(codStatus, pageable)
                    .map(modalityMapper::toDomain);
        }
        return repository.findAll(pageable)
                .map(modalityMapper::toDomain);
    }

    @Override
    public Modality save(Modality modality) {
        var entity = modalityMapper.toEntity(modality);
        var saved = repository.save(entity);
        return modalityMapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codModalidade, String codStatus, Long idUsuario) {
        repository.findById(codModalidade).ifPresent(entity -> {
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
    public void deleteById(String codModalidade) {
        repository.deleteById(codModalidade);
    }
}


