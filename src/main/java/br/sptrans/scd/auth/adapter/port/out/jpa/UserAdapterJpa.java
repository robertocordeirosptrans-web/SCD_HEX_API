package br.sptrans.scd.auth.adapter.port.out.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.port.out.jpa.entity.UserEntityJpa;
import br.sptrans.scd.auth.adapter.port.out.jpa.mapper.UserMapper;
import br.sptrans.scd.auth.adapter.port.out.jpa.repository.UserRepositoryJpa;
import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.User;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserAdapterJpa implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserAdapterJpa.class);

    private final UserRepositoryJpa userRepositoryJpa;

    @Override
    public Optional<User> findByCodLogin(String codLogin) {
        Optional<UserEntityJpa> entityOpt = userRepositoryJpa.findByCodLogin(codLogin);
        return entityOpt.map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByLogin(String codLogin) {
        return userRepositoryJpa.existsByCodLogin(codLogin);
    }

    @Override
    public void atualizarTentativasEStatus(Long idUsuario, int numTentativas, String codStatus) {
        userRepositoryJpa.atualizarTentativasEStatus(idUsuario, numTentativas, codStatus);
    }

    @Override
    public void atualizarUltimoAcesso(Long idUsuario) {
        userRepositoryJpa.atualizarUltimoAcesso(idUsuario);
    }

    @Override
    public void updateStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
        userRepositoryJpa.updateStatus(idUsuario, codStatus);

    }

    @Override
    public void updatePassword(Long idUsuario, String newPasswordHash, String oldPasswordHash, LocalDateTime expiryDate) {
        userRepositoryJpa.updatePassword(idUsuario, newPasswordHash, oldPasswordHash, expiryDate);
    }

    @Override
    public void resetAttemptsAndStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
        userRepositoryJpa.resetAttemptsAndStatus(idUsuario, codStatus);
    }

    @Override
    public void updateAccessSchedule(Long idUsuario, String diasPermitidos, Date jornadaIni, Date jornadaFim, Long idUsuarioManutencao) {
        userRepositoryJpa.updateAccessSchedule(idUsuario, diasPermitidos, jornadaIni, jornadaFim);
    }

    @Override
    public User save(User user) {
        UserEntityJpa entity = UserMapper.toEntity(user);
        UserEntityJpa saved = userRepositoryJpa.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepositoryJpa.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByNomEmail(String nomEmail) {
        return userRepositoryJpa.findByNomEmail(nomEmail).map(UserMapper::toDomain);
    }

    @Override
    public void update(User usuario) {
        userRepositoryJpa.save(UserMapper.toEntity(usuario));
    }

    @Override
    public List<User> findAllPaginated(String status, String nome, String email, String perfil, int offset, int limit, String sortBy, String sortDir) {

        List<UserEntityJpa> entities = userRepositoryJpa.findAllByCodStatus(status);
        List<User> users = new ArrayList<>();
        for (UserEntityJpa entity : entities) {
            if (nome == null || entity.getNomUsuario().toLowerCase().contains(nome.toLowerCase())) {
                users.add(UserMapper.toDomain(entity));
            }

            if (email == null || entity.getNomEmail().toLowerCase().contains(email.toLowerCase())) {
                users.add(UserMapper.toDomain(entity));
            }

            if (status == null || entity.getCodStatus().toLowerCase().contains(status.toLowerCase())) {
                users.add(UserMapper.toDomain(entity));
            }
        }
        // Paginação manual
        int toIndex = Math.min(offset + limit, users.size());
        if (offset > toIndex) {
            return new ArrayList<>();
        }
        return users.subList(offset, toIndex);
    }

    @Override
    public long countAll(String status, String nome, String email, String perfil) {
        List<UserEntityJpa> entities = userRepositoryJpa.findAllByCodStatus(status);
        return entities.stream().filter(e -> nome == null || e.getNomUsuario().toLowerCase().contains(nome.toLowerCase())).count();
    }

    @Override
    public boolean hasActiveSession(Long idUsuario) {
        Optional<UserEntityJpa> entityOpt = userRepositoryJpa.findById(idUsuario);
        if (entityOpt.isPresent()) {
            UserEntityJpa entity = entityOpt.get();
            if (entity.getDtUltimoAcesso() == null) {
                return false;
            }
            return entity.getDtUltimoAcesso().isAfter(java.time.LocalDateTime.now().minusMinutes(30));
        }
        return false;
    }

    @Override
    public Set<Functionality> carregarFuncionalidadesEfetivas(Long idUsuario) {
        // TODO: Implementar busca de funcionalidades efetivas via JOINs
        return new HashSet<>();
    }

    @Override
    public Set<Profile> carregarPerfisEfetivos(Long idUsuario) {
        // TODO: Implementar busca de perfis efetivos via JOINs
        return new HashSet<>();
    }

}
