package br.sptrans.scd.auth.adapter.port.out.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.port.out.jpa.mapper.UserMapper;
import br.sptrans.scd.auth.adapter.port.out.jpa.repository.UserRepositoryJpa;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.application.port.out.AuthenticationPort;

import br.sptrans.scd.auth.application.port.out.AuthorizationPort;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;


import br.sptrans.scd.auth.application.port.out.UserStatusPort;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.User;
import lombok.RequiredArgsConstructor;

/**
 * Adapter — Único ponto de persistência de User (hexagonal).
 *
 * Implementa {@link UserPersistencePort} (novo contrato ISP) e
 * {@link UserRepository} (contrato legado), eliminando dependências
 * circulares que existiam quando este adapter delegava para sub-ports.
 *
 * Operações de autenticação, status e autorização são delegadas aos
 * adapters especializados já existentes, que possuem {@code @Primary}
 * e são injetados aqui sem risco de auto-injeção.
 */
@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {



    private final UserRepositoryJpa userRepositoryJpa;
    private final AuthenticationPort authenticationPort;
    private final UserStatusPort userStatusPort;
    private final AuthorizationPort authorizationPort;

    // ────────────────────────────────────────────────────────────────────────────
    // UserQueryPort — leitura (UserPersistencePort)
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    public Optional<User> findById(Long userId) {
        return userRepositoryJpa.findById(userId).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        return userRepositoryJpa.findByCodCpf(cpf).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByCodLogin(String codLogin) {
        return userRepositoryJpa.findByCodLogin(codLogin).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByNomEmail(String nomEmail) {
        return userRepositoryJpa.findByNomEmail(nomEmail).map(UserMapper::toDomain);
    }

    @Override
    public Object getUserProfile(Long userId) {
        return userRepositoryJpa.findById(userId).map(UserMapper::toDomain).orElse(null);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // UserReader — métodos adicionais (UserRepository legado)
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    public boolean existsByLogin(String codLogin) {
        return userRepositoryJpa.existsByCodLogin(codLogin);
    }

    @Override
    public List<User> findAllPaginated(String status, String nome, String email, String perfil,
                                       int offset, int limit, String sortBy, String sortDir) {
        List<UserEntityJpa> entities = userRepositoryJpa.findAllByCodStatus(status);
        List<User> users = new ArrayList<>();
        for (UserEntityJpa entity : entities) {
            if (nome == null || entity.getNomUsuario().toLowerCase().contains(nome.toLowerCase())) {
                users.add(UserMapper.toDomain(entity));
            } else if (email == null || entity.getNomEmail().toLowerCase().contains(email.toLowerCase())) {
                users.add(UserMapper.toDomain(entity));
            } else if (status == null || entity.getCodStatus().toLowerCase().contains(status.toLowerCase())) {
                users.add(UserMapper.toDomain(entity));
            }
        }
        int toIndex = Math.min(offset + limit, users.size());
        if (offset > toIndex) {
            return new ArrayList<>();
        }
        return users.subList(offset, toIndex);
    }

    @Override
    public long countAll(String status, String nome, String email, String perfil) {
        List<UserEntityJpa> entities = userRepositoryJpa.findAllByCodStatus(status);
        return entities.stream()
                .filter(e -> nome == null || e.getNomUsuario().toLowerCase().contains(nome.toLowerCase()))
                .count();
    }

    // ────────────────────────────────────────────────────────────────────────────
    // UserCommandPort + UserWriter — escrita
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public User save(User user) {
        UserEntityJpa entity = UserMapper.toEntity(user);
        UserEntityJpa saved = userRepositoryJpa.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public void delete(Long userId) {
        userRepositoryJpa.deleteById(userId);
    }

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public void update(User user) {
        userRepositoryJpa.save(UserMapper.toEntity(user));
    }

    @Override
    public void updatePassword(Long userId, String hashedPassword) {
        userRepositoryJpa.updatePassword(userId, hashedPassword, null, null);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // AuthenticationPort + AuthenticationRepository — delegar ao adapter @Primary
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    public void atualizarTentativasEStatus(Long idUsuario, int numTentativas, String codStatus) {
        authenticationPort.atualizarTentativasEStatus(idUsuario, numTentativas, codStatus);
    }

    @Override
    public void atualizarUltimoAcesso(Long idUsuario) {
        authenticationPort.atualizarUltimoAcesso(idUsuario);
    }

    @Override
    public void resetAttemptsAndStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
        authenticationPort.resetAttemptsAndStatus(idUsuario, codStatus, idUsuarioManutencao);
    }

    @Override
    public boolean hasActiveSession(Long idUsuario) {
        return authenticationPort.hasActiveSession(idUsuario);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // UserStatusPort + UserStatusRepository — delegar ao adapter @Primary
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    public void updateStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
        userStatusPort.updateStatus(idUsuario, codStatus, idUsuarioManutencao);
    }

    @Override
    public void updatePassword(Long idUsuario, String newPasswordHash, String oldPasswordHash,
            LocalDateTime expiryDate) {
        userStatusPort.updatePassword(idUsuario, newPasswordHash, oldPasswordHash, expiryDate);
    }

    @Override
    public void updateAccessSchedule(Long idUsuario, String diasPermitidos, Date jornadaIni,
            Date jornadaFim, Long idUsuarioManutencao) {
        userStatusPort.updateAccessSchedule(idUsuario, diasPermitidos, jornadaIni, jornadaFim,
                idUsuarioManutencao);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // AuthorizationPort + AuthorizationRepository — delegar ao adapter @Primary
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    public Set<Functionality> carregarFuncionalidadesEfetivas(Long idUsuario) {
        return authorizationPort.carregarFuncionalidadesEfetivas(idUsuario);
    }

    @Override
    public Set<Profile> carregarPerfisEfetivos(Long idUsuario) {
        return authorizationPort.carregarPerfisEfetivos(idUsuario);
    }
}

