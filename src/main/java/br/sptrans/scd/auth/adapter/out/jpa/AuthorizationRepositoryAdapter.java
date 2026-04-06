package br.sptrans.scd.auth.adapter.out.jpa;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.adapter.out.jpa.mapper.FunctionalityMapper;
import br.sptrans.scd.auth.adapter.out.jpa.mapper.ProfileMapper;
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
public class AuthorizationRepositoryAdapter implements AuthorizationPort {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationRepositoryAdapter.class);

    private final UserProfileJpaRepository userProfileJpaRepository;

    @Override
    @Cacheable(value = "permissoes", key = "'func:' + #idUsuario")
    public Set<Functionality> carregarFuncionalidadesEfetivas(Long idUsuario) {
        log.debug("Carregando funcionalidades efetivas para usuário: {}", idUsuario);
        
        try {
            var userProfiles = userProfileJpaRepository.findActiveWithFunctionalities(idUsuario);
            
            Set<Functionality> functionalities = userProfiles.stream()
                .map(up -> up.getPerfil())
                .filter(perfil -> perfil != null)
                .flatMap(perfil -> perfil.getPerfilFuncionalidades().stream())
                .filter(pf -> pf.getFuncionalidade() != null)
                .map(pf -> FunctionalityMapper.toDomain(pf.getFuncionalidade()))
                .collect(java.util.stream.Collectors.toSet());
            
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
                .map(ProfileMapper::toDomain)
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
}
