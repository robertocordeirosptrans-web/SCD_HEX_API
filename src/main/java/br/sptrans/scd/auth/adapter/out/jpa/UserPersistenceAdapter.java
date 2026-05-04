package br.sptrans.scd.auth.adapter.out.jpa;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.in.rest.dto.UserFilterRequestDTO;
import br.sptrans.scd.auth.adapter.out.jpa.mapper.UserMapper;
import br.sptrans.scd.auth.adapter.out.jpa.repository.UserRepositoryJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.adapter.specification.UserSpecification;
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
 * Implementa {@link UserPersistencePort} eliminando dependências
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
    private final UserMapper userMapper;

    // ────────────────────────────────────────────────────────────────────────────
    // UserQueryPort — leitura (UserPersistencePort)
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    public Optional<User> findById(Long userId) {
        return userRepositoryJpa.findById(userId).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        return userRepositoryJpa.findByCodCpf(cpf).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByCodLogin(String codLogin) {
        return userRepositoryJpa.findByCodLogin(codLogin).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByNomEmail(String nomEmail) {
        return userRepositoryJpa.findByNomEmail(nomEmail).map(userMapper::toDomain);
    }

    @Override
    public Object getUserProfile(Long userId) {
        return userRepositoryJpa.findById(userId).map(userMapper::toDomain).orElse(null);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // UserReader — métodos adicionais (UserRepository legado)
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    public boolean existsByLogin(String codLogin) {
        return userRepositoryJpa.existsByCodLogin(codLogin);
    }

    @Override
    public List<User> findAllPaginated(String status, String nome, String email, String perfil, int offset, int limit, String sortBy, String sortDir) {
        UserFilterRequestDTO filtro = new UserFilterRequestDTO(nome, email, perfil, status);
        // Usar Specification para UserEntityJpa, não User
        Specification<UserEntityJpa> spec = UserSpecification.filterUsers(filtro);
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.Direction.fromString(sortDir), sortBy);
        List<UserEntityJpa> entities = userRepositoryJpa.findAll(spec, pageable).getContent();
        if (entities == null || entities.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return entities.stream().map(userMapper::toDomain).toList();
    }

    @Override
    public long countAll(String status, String nome, String email, String perfil) {
        List<UserEntityJpa> entities = userRepositoryJpa.findAllByCodStatus(status);
        return entities.stream()
                .filter(e -> nome == null || e.getNomUsuario().toLowerCase().contains(nome.toLowerCase()))
                .count();
    }

    @Override
    public Page<User> findAllPaginated(Specification<UserEntityJpa> spec, Pageable pageable) {
        return userRepositoryJpa.findAll(spec, pageable).map(userMapper::toDomain);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // UserCommandPort + UserWriter — escrita
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public User save(User user) {
        UserEntityJpa entity = userMapper.toEntity(user);
        
        // Gerar ID_USUARIO automaticamente se não foi informado
        if (entity.getIdUsuario() == null || entity.getIdUsuario() == 0) {
            Long maxId = userRepositoryJpa.findMaxIdUsuario();
            entity.setIdUsuario(maxId + 1);
        }
        
        UserEntityJpa saved = userRepositoryJpa.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public void delete(Long userId) {
        userRepositoryJpa.deleteById(userId);
    }

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public void update(User user) {
        userRepositoryJpa.save(userMapper.toEntity(user));
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

