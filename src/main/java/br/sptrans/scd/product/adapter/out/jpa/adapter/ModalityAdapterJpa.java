package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.adapter.out.jpa.mapper.ModalityMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.ModalityJpaRepository;
import br.sptrans.scd.product.application.port.out.repository.ModalityRepository;
import br.sptrans.scd.product.domain.Modality;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ModalityAdapterJpa implements ModalityRepository {

    private final ModalityJpaRepository repository;
    private final UserPersistencePort userRepository;

    @Override
    public Optional<Modality> findById(String codModalidade) {
        return repository.findById(codModalidade)
                .map(entity -> ModalityMapper.toDomain(entity, userRepository));
    }

    @Override
    public boolean existsById(String codModalidade) {
        return repository.existsById(codModalidade);
    }

    @Override
    public List<Modality> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(entity -> ModalityMapper.toDomain(entity, userRepository))
                    .filter(m -> codStatus.equals(m.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(entity -> ModalityMapper.toDomain(entity, userRepository))
                .toList();
    }

    @Override
    public Modality save(Modality modality) {
        var entity = ModalityMapper.toEntity(modality);
        var saved = repository.save(entity);
        return ModalityMapper.toDomain(saved, userRepository);
    }

    @Override
    public void updateStatus(String codModalidade, String codStatus, Long idUsuario) {
        repository.findById(codModalidade).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            if (idUsuario != null) {
                User user = new User();
                user.setIdUsuario(idUsuario);
                entity.setIdUsuarioManutencao(user.getIdUsuario());
            }
            repository.save(entity);
        });
    }

    @Override
    public void deleteById(String codModalidade) {
        repository.deleteById(codModalidade);
    }
}


