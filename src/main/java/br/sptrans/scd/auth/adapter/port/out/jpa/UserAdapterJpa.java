package br.sptrans.scd.auth.adapter.port.out.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.port.out.jpa.mapper.FunctionalityMapper;
import br.sptrans.scd.auth.adapter.port.out.jpa.mapper.ProfileMapper;
import br.sptrans.scd.auth.adapter.port.out.jpa.mapper.UserMapper;
import br.sptrans.scd.auth.adapter.port.out.jpa.repository.UserProfileJpaRepository;
import br.sptrans.scd.auth.adapter.port.out.jpa.repository.UserRepositoryJpa;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.UserProfileJpa;
import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.shared.exception.InvalidUserProfileException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserAdapterJpa implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserAdapterJpa.class);

    private final UserRepositoryJpa userRepositoryJpa;
    private final UserProfileJpaRepository userProfileJpaRepository;

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
    @CacheEvict(value = "permissoes", allEntries = true)
    public void updateStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
        userRepositoryJpa.updateStatus(idUsuario, codStatus);
    }

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public void updatePassword(Long idUsuario, String newPasswordHash, String oldPasswordHash, LocalDateTime expiryDate) {
        userRepositoryJpa.updatePassword(idUsuario, newPasswordHash, oldPasswordHash, expiryDate);
    }

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public void resetAttemptsAndStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
        userRepositoryJpa.resetAttemptsAndStatus(idUsuario, codStatus);
    }

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public void updateAccessSchedule(Long idUsuario, String diasPermitidos, Date jornadaIni, Date jornadaFim, Long idUsuarioManutencao) {
        userRepositoryJpa.updateAccessSchedule(idUsuario, diasPermitidos, jornadaIni, jornadaFim);
    }

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
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
    @CacheEvict(value = "permissoes", allEntries = true)
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
    @Cacheable(value = "permissoes", key = "'func:' + #idUsuario")
    public Set<Functionality> carregarFuncionalidadesEfetivas(Long idUsuario) {
        // Única query com JOIN FETCH evita N+1 (perfis → funcionalidades)
        List<UserProfileJpa> userProfiles = userProfileJpaRepository.findActiveWithFunctionalities(idUsuario);
        return userProfiles.stream()
            .map(UserProfileJpa::getPerfil)
            .filter(perfil -> perfil != null)
            .flatMap(perfil -> perfil.getPerfilFuncionalidades().stream())
            .filter(pf -> pf.getFuncionalidade() != null)
            .map(pf -> FunctionalityMapper.toDomain(pf.getFuncionalidade()))
            .collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = "permissoes", key = "'perfis:' + #idUsuario")
    public Set<Profile> carregarPerfisEfetivos(Long idUsuario) {
        List<UserProfileJpa> userProfiles = userProfileJpaRepository.findByUsuarioIdUsuarioAndCodStatus(idUsuario, "A");
        long nullCount = userProfiles.stream().filter(up -> up == null).count();
        log.info("[carregarPerfisEfetivos] Perfis encontrados para usuário {}: {} (nulos: {})", idUsuario, userProfiles.size(), nullCount);

        Set<Profile> perfisValidos = userProfiles.stream()
            .filter(up -> up != null)
            .filter(up -> {
                // Ignora perfis expirados
                if (up.getId() != null && up.getId().getDtFimValidade() != null) {
                    if (up.getId().getDtFimValidade().isBefore(java.time.LocalDateTime.now())) {
                        log.warn("Perfil expirado para usuário {}: {} (dtFimValidade={})", idUsuario, up, up.getId().getDtFimValidade());
                        return false;
                    }
                }
                return true;
            })
            .peek(up -> {
                if (up.getPerfil() == null) {
                    log.warn("UserProfileJpa com perfil nulo para usuário {}: {}", idUsuario, up);
                } else {
                    log.info("[carregarPerfisEfetivos] Perfil encontrado: codPerfil={}, codStatus={}", up.getPerfil().getCodPerfil(), up.getPerfil().getCodStatus());
                }
            })
            .map(UserProfileJpa::getPerfil)
            .filter(perfil -> {
                if (perfil == null) {
                    log.warn("UserProfileJpa com perfil nulo encontrado para usuário {}", idUsuario);
                    return false;
                }
                return "A".equalsIgnoreCase(perfil.getCodStatus());
            })
            .map(ProfileMapper::toDomain)
            .collect(Collectors.toSet());

        if (perfisValidos.isEmpty()) {
            throw new InvalidUserProfileException(
                "Usuário sem associação válida com perfil (expirado, inativo ou inexistente)"
            );
        }
        return perfisValidos;
    }

}
