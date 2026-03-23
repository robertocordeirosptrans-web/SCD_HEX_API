package br.sptrans.scd.product.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.product.adapter.out.jpa.mapper.TechnologyMapper;
import br.sptrans.scd.product.adapter.out.jpa.repository.TechnologyJpaRepository;
import br.sptrans.scd.product.application.port.out.TechnologyRepository;
import br.sptrans.scd.product.domain.Technology;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class TechnologyAdapterJpa implements TechnologyRepository {

    private final TechnologyJpaRepository repository;
    private final UserRepository userRepository;

    @Override
    public Optional<Technology> findById(String codTecnologia) {
        return repository.findById(codTecnologia)
                .map(entity -> TechnologyMapper.toDomain(entity, userRepository));
    }

    @Override
    public boolean existsById(String codTecnologia) {
        return repository.existsById(codTecnologia);
    }

    @Override
    public List<Technology> findAll(String codStatus) {
        if (codStatus != null && !codStatus.isBlank()) {
            return repository.findAll().stream()
                    .map(entity -> TechnologyMapper.toDomain(entity, userRepository))
                    .filter(t -> codStatus.equals(t.getCodStatus()))
                    .toList();
        }
        return repository.findAll().stream()
                .map(entity -> TechnologyMapper.toDomain(entity, userRepository))
                .toList();
    }

    @Override
    public Technology save(Technology technology) {
        var entity = TechnologyMapper.toEntity(technology);
        var saved = repository.save(entity);
        return TechnologyMapper.toDomain(saved, userRepository);
    }

    @Override
    public void updateStatus(String codTecnologia, String codStatus, Long idUsuario) {
        repository.findById(codTecnologia).ifPresent(entity -> {
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
    public void deleteById(String codTecnologia) {
        repository.deleteById(codTecnologia);
    }
}
