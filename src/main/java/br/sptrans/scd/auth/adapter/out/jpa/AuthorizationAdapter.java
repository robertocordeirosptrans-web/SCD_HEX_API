package br.sptrans.scd.auth.adapter.out.jpa;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.adapter.out.jpa.mapper.FunctionalityMapper;
import br.sptrans.scd.auth.adapter.out.jpa.mapper.ProfileMapper;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupUserJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.UserFunctionalityJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.UserProfileJpaRepository;
import br.sptrans.scd.auth.application.port.out.AuthorizationPort;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.shared.exception.InvalidUserProfileException;
import lombok.RequiredArgsConstructor;

/**
 * Adapter — Repositório de Autorização (Perfis e Funcionalidades)
 * 
 * Responsável por: - Carregar funcionalidades efetivas do usuário
 * - Carregar perfis efetivos do usuário - Combinar múltiplas fontes:
 * uso_perfis, grupo_perfis, func_diretas
 * 
 * Implementa: AuthorizationRepository
 */
@Primary
@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthorizationAdapter implements AuthorizationPort {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationAdapter.class);

    private final UserProfileJpaRepository userProfileJpaRepository;
    private final GroupUserJpaRepository groupUserJpaRepository;
    private final UserFunctionalityJpaRepository userFunctionalityJpaRepository;
    private final FunctionalityMapper functionalityMapper;
    private final ProfileMapper profileMapper;

    @Override
    @Cacheable(value = "permissoes", key = "'func:' + #idUsuario")
    public Set<Functionality> carregarFuncionalidadesEfetivas(Long idUsuario) {
        log.debug("Carregando funcionalidades efetivas para usuário: {}", idUsuario);

        try {
            Set<Functionality> functionalities = new java.util.HashSet<>();

            // Fonte #1: Perfis diretos do usuário → funcionalidades (USUARIO_PERFIS)
            var userProfiles = userProfileJpaRepository.findActiveWithFunctionalities(idUsuario);
            userProfiles.stream()
                .map(up -> up.getPerfil())
                .filter(perfil -> perfil != null)
                .flatMap(perfil -> perfil.getPerfilFuncionalidades().stream())
                .filter(pf -> pf.getFuncionalidade() != null)
                .map(pf -> functionalityMapper.toDomain(pf.getFuncionalidade()))
                .forEach(functionalities::add);

            // Fonte #2: Grupos do usuário → perfis → funcionalidades (GRUPO_USUARIOS → GRUPO_PERFIS → PERFIL_FUNCIONALIDADES)
            var groupUsers = groupUserJpaRepository.findActiveWithFunctionalities(idUsuario);
            groupUsers.stream()
                .map(gu -> gu.getGrupo())
                .filter(grupo -> grupo != null)
                .flatMap(grupo -> grupo.getGrupoPerfis().stream())
                .filter(gp -> gp.getPerfil() != null)
                .flatMap(gp -> gp.getPerfil().getPerfilFuncionalidades().stream())
                .filter(pf -> pf.getFuncionalidade() != null)
                .map(pf -> functionalityMapper.toDomain(pf.getFuncionalidade()))
                .forEach(functionalities::add);

            // Fonte #3: Funcionalidades diretas do usuário (USUARIO_FUNCIONALIDADES)
            var directFuncs = userFunctionalityJpaRepository.findActiveByUsuario(idUsuario);
            directFuncs.stream()
                .map(uf -> uf.getFuncionalidade())
                .filter(f -> f != null)
                .map(functionalityMapper::toDomain)
                .forEach(functionalities::add);

            log.debug("Funcionalidades carregadas para usuário {}: {} itens", idUsuario, functionalities.size());
            return functionalities;
        } catch (Exception e) {
            log.error("Erro ao carregar funcionalidades efetivas para usuário: {}", idUsuario, e);
            throw new InvalidUserProfileException("Erro ao carregar funcionalidades do usuário", e);
        }
    }

    @Override
    @Cacheable(value = "permissoes", key = "'perfis:' + #idUsuario")
    public Set<Profile> carregarPerfisEfetivos(Long idUsuario) {
        log.debug("Carregando perfis efetivos para usuário: {}", idUsuario);
        
        try {
            var userProfiles = userProfileJpaRepository.findByUsuarioIdUsuarioAndCodStatus(idUsuario, "A");
            
            Set<Profile> perfisValidos = userProfiles.stream()
                .filter(up -> up != null)
                .filter(up -> {
                    // Ignora perfis expirados
                    if (up.getId() != null && up.getId().getDtFimValidade() != null) {
                        if (up.getId().getDtFimValidade().isBefore(java.time.LocalDateTime.now())) {
                            log.warn("Perfil expirado para usuário {}: dtFimValidade={}", 
                                idUsuario, up.getId().getDtFimValidade());
                            return false;
                        }
                    }
                    return true;
                })
                .map(up -> up.getPerfil())
                .filter(perfil -> perfil != null && "A".equalsIgnoreCase(perfil.getCodStatus()))
                .map(profileMapper::toDomain)
                .collect(java.util.stream.Collectors.toSet());
            
            if (perfisValidos.isEmpty()) {
                log.warn("Usuário {} sem perfis ativos válidos", idUsuario);
                throw new InvalidUserProfileException(
                    "Usuário sem associação válida com perfil (expirado, inativo ou inexistente)"
                );
            }
            
            log.debug("Perfis carregados para usuário {}: {} itens", idUsuario, perfisValidos.size());
            return perfisValidos;
        } catch (InvalidUserProfileException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao carregar perfis efetivos para usuário: {}", idUsuario, e);
            throw new InvalidUserProfileException("Erro ao carregar perfis do usuário", e);
        }
    }
    /**
     * Remove permissões e perfis do cache para o usuário informado.
     * Pode ser chamado após logout ou alteração de permissões.
     */
    @Caching(evict = {
        @CacheEvict(value = "permissoes", key = "'func:' + #idUsuario"),
        @CacheEvict(value = "permissoes", key = "'perfis:' + #idUsuario")
    })
    public void evictUserPermissions(Long idUsuario) {
        log.info("Cache de permissões invalidado para usuário {}", idUsuario);
    }
}
