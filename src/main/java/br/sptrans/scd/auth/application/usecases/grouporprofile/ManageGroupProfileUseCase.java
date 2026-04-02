package br.sptrans.scd.auth.application.usecases.grouporprofile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.application.port.out.GroupPort;
import br.sptrans.scd.auth.application.port.out.GroupUserPort;

import br.sptrans.scd.auth.application.port.out.ProfilePort;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.shared.exception.BusinessException;
import br.sptrans.scd.shared.exception.DuplicateResourceException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Use Case — Gerenciar Grupos e Perfis
 * 
 * Responsável por: - Criar/atualizar/ativar/inativar grupos - Criar/atualizar/ativar/inativar
 * perfis - Associar funcionalidades a perfis - Associar perfis a grupos -
 * Validações de integridade
 * 
 * Regras de negócio: - Grupos possuem perfis; perfis possuem funcionalidades -
 * Inativar grupo NÃO inativa seus perfis (compartilhados) - COD_GRUPO e
 * COD_PERFIL são únicos e imutáveis - Impossível inativar grupo/perfil com
 * usuários ativos vinculados
 * 
 * Portos utilizados: - Output Port: GroupRepository — CRUD de grupos -
 * Output Port: ProfileRepository — CRUD de perfis - Output Port:
 * GroupProfileRepository — vínculos grupo-perfil - Output Port:
 * GroupUserRepository — validar usuários ativos
 */
@Component
@Transactional
@RequiredArgsConstructor
public class ManageGroupProfileUseCase {

    private static final Logger log = LoggerFactory.getLogger(ManageGroupProfileUseCase.class);

    private final GroupPort groupRepository;
    private final ProfilePort profileRepository;
    private final GroupUserPort groupUserRepository;

    // ══════════════════════════════════════════════════════════════════════════
    // GRUPOS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Cria novo grupo.
     * 
     * Regras: - COD_GRUPO deve ser único
     * 
     * @param command contém código e nome do novo grupo
     * @return grupo criado
     * @throws DuplicateResourceException se COD_GRUPO já existe
     */
    public Group createGroup(GroupProfileManagementUseCase.CreateGroupCommand command) {
        log.info("Criando novo grupo. Código: {}", command.codGrupo());
        
        if (groupRepository.existsByCode(command.codGrupo())) {
            log.warn("Código de grupo duplicado: {}", command.codGrupo());
            throw new DuplicateResourceException(
                "Grupo", "codGrupo", command.codGrupo());
        }

        Group grupo = new Group();
        grupo.setCodGrupo(command.codGrupo().toUpperCase().trim());
        grupo.setNomGrupo(command.nomGrupo().trim());
        grupo.setCodStatus("A"); // Inicia ativo

        groupRepository.save(grupo);
        log.info("Grupo criado. Código: {}", grupo.getCodGrupo());
        
        return grupo;
    }

    /**
     * Atualiza nome do grupo.
     * 
     * Regras: - COD_GRUPO é imutável
     * 
     * @param command contém código e novo nome
     * @return grupo atualizado
     * @throws ResourceNotFoundException se grupo não encontrado
     */
    public Group updateGroup(GroupProfileManagementUseCase.UpdateGroupCommand command) {
        log.info("Atualizando grupo. Código: {}", command.codGrupo());
        
        Group grupo = findGroupOrThrow(command.codGrupo());

        grupo.setNomGrupo(command.nomGrupo().trim());

        groupRepository.save(grupo);
        log.info("Grupo atualizado. Código: {}", grupo.getCodGrupo());
        
        return grupo;
    }

    /**
     * Inativa grupo.
     * 
     * Regras: - Lança exceção se houver usuários ativos vinculados
     * 
     * @param command contém código do grupo
     * @throws BusinessException se usuários ativos vinculados
     */
    public void deactivateGroup(GroupProfileManagementUseCase.DeactivateCommand command) {
        log.info("Desativando grupo. Código: {}", command.code());
        
        Group grupo = findGroupOrThrow(command.code());

        if (!grupo.isActive()) {
            log.warn("Grupo já está inativo. Código: {}", command.code());
            throw new BusinessException(
                "O grupo '" + command.code() + "' já está inativo.",
                "ALREADY_INACTIVE");
        }

        // Valida se há usuários ativos vinculados
        long usuariosAtivos = groupRepository.countUserActive(command.code());
        if (usuariosAtivos > 0) {
            log.warn("Tentativa de inativar grupo com usuários ativos. Código: {}, Usuários: {}", 
                command.code(), usuariosAtivos);
            throw new BusinessException(
                "Não é possível inativar o grupo '" + command.code()
                    + "': " + usuariosAtivos + " usuário(s) ativo(s) vinculado(s).",
                "HAS_ACTIVE_USERS");
        }

        groupRepository.updateStatus(
            command.code(), "I", command.idUsuarioLogado());
        
        log.info("Grupo desativado. Código: {}", command.code());
    }

    /**
     * Reativa grupo inativo.
     * 
     * @param command contém código do grupo
     * @throws BusinessException se grupo já está ativo
     */
    public void reactivateGroup(GroupProfileManagementUseCase.ReactivateCommand command) {
        log.info("Reativando grupo. Código: {}", command.code());
        
        Group grupo = findGroupOrThrow(command.code());

        if (grupo.isActive()) {
            log.warn("Grupo já está ativo. Código: {}", command.code());
            throw new BusinessException(
                "O grupo '" + command.code() + "' já está ativo.",
                "ALREADY_ACTIVE");
        }

        groupRepository.updateStatus(
            command.code(), "A", command.idUsuarioLogado());
        
        log.info("Grupo reativado. Código: {}", command.code());
    }

    /**
     * Associa um ou mais perfis ao grupo.
     * 
     * @param command contém código do grupo e lista de perfis
     * @throws DuplicateResourceException se perfil já associado
     * @throws ResourceNotFoundException se grupo ou perfil não encontrado
     */
    @CacheEvict(value = "permissoes", allEntries = true)
    public void associateProfilesToGroup(
        GroupProfileManagementUseCase.AssociateProfilesToGroupCommand command) {
        
        log.info("Associando perfis ao grupo. Código: {}", command.groupCode());
        
        findGroupOrThrow(command.groupCode());

        for (String codPerfil : command.profileCodes()) {
            // Valida existência do perfil
            profileRepository.findById(codPerfil)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil", "codPerfil", codPerfil));

            // Valida se já está associado
            if (groupRepository.isProfileAssociate(command.groupCode(), codPerfil)) {
                log.warn("Perfil já associado ao grupo. Grupo: {}, Perfil: {}",
                    command.groupCode(), codPerfil);
                throw new DuplicateResourceException(
                    "Perfil '" + codPerfil
                        + "' já está associado ao grupo '" + command.groupCode() + "'.");
            }

            groupRepository.associateProfilesToGroup(
                command.groupCode(), 
                codPerfil, 
                command.idUsuarioLogado());
            
            log.info("Perfil associado. Grupo: {}, Perfil: {}", 
                command.groupCode(), codPerfil);
        }
    }

    /**
     * Remove associação de perfil do grupo.
     * 
     * @param command contém código do grupo e perfil
     * @throws ResourceNotFoundException se grupo ou perfil não encontrado
     */
    @CacheEvict(value = "permissoes", allEntries = true)
    public void disassociateProfileFromGroup(
        GroupProfileManagementUseCase.DisassociateProfileFromGroupCommand command) {
        
        log.info("Removendo associação de perfil. Grupo: {}, Perfil: {}",
            command.groupCode(), command.profileCode());
        
        findGroupOrThrow(command.groupCode());
        findProfileOrThrow(command.profileCode());

        groupRepository.disassociateProfileFromGroup(
            command.groupCode(), 
            command.profileCode(), 
            command.idUsuarioLogado());
        
        log.info("Associação removida. Grupo: {}, Perfil: {}", 
            command.groupCode(), command.profileCode());
    }

    /**
     * Lista funcionalidades disponíveis.
     * 
     * @return lista de todas as funcionalidades
     */
    public List<Functionality> listFunctionalities() {
        log.debug("Listando funcionalidades disponíveis");
        return profileRepository.listFunctionalityActive();
    }

    /**
     * Lista todos os grupos, opcionalmente filtrado por status.
     * 
     * @param statusCode status de filtro (null = todos)
     * @return lista de grupos
     */
    public List<Group> listGroups(String statusCode) {
        log.debug("Listando grupos. Status: {}", statusCode);
        return groupRepository.listGroups(statusCode);
    }

    /**
     * Lista usuários ativos de um grupo.
     * 
     * @param codGrupo código do grupo
     * @return lista de usuários ativos
     */
    public List<GroupUser> listGroupUsersByCodGrupo(String codGrupo) {
        log.debug("Listando usuários ativos do grupo: {}", codGrupo);
        return groupUserRepository
            .findById_CodGrupoAndCodStatus(codGrupo, "A");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PERFIS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Cria novo perfil.
     * 
     * Regras: - COD_PERFIL deve ser único
     * 
     * @param command contém código e nome do novo perfil
     * @return perfil criado
     * @throws DuplicateResourceException se COD_PERFIL já existe
     */
    public Profile createProfile(GroupProfileManagementUseCase.CreateProfileCommand command) {
        log.info("Criando novo perfil. Código: {}", command.codPerfil());
        
        if (profileRepository.existsByCode(command.codPerfil())) {
            log.warn("Código de perfil duplicado: {}", command.codPerfil());
            throw new DuplicateResourceException(
                "Perfil", "codPerfil", command.codPerfil());
        }

        Profile perfil = new Profile();
        perfil.setCodPerfil(command.codPerfil().toUpperCase().trim());
        perfil.setNomPerfil(command.nomPerfil().trim());
        perfil.setCodStatus("A"); // Inicia ativo

        profileRepository.save(perfil);
        log.info("Perfil criado. Código: {}", perfil.getCodPerfil());
        
        return perfil;
    }

    /**
     * Atualiza nome do perfil.
     * 
     * Regras: - COD_PERFIL é imutável
     * 
     * @param command contém código e novo nome
     * @return perfil atualizado
     * @throws ResourceNotFoundException se perfil não encontrado
     */
    public Profile updateProfile(GroupProfileManagementUseCase.UpdateProfileCommand command) {
        log.info("Atualizando perfil. Código: {}", command.codPerfil());
        
        Profile perfil = findProfileOrThrow(command.codPerfil());

        perfil.setNomPerfil(command.nomPerfil().trim());

        profileRepository.save(perfil);
        log.info("Perfil atualizado. Código: {}", perfil.getCodPerfil());
        
        return perfil;
    }

    /**
     * Inativa perfil.
     * 
     * Regras: - Lança exceção se houver usuários ativos vinculados diretamente
     * 
     * @param command contém código do perfil
     * @throws BusinessException se usuários ativos vinculados
     */
    public void deactivateProfile(GroupProfileManagementUseCase.DeactivateCommand command) {
        log.info("Desativando perfil. Código: {}", command.code());
        
        Profile perfil = findProfileOrThrow(command.code());

        if (!perfil.isActive()) {
            log.warn("Perfil já está inativo. Código: {}", command.code());
            throw new BusinessException(
                "O perfil '" + command.code() + "' já está inativo.",
                "ALREADY_INACTIVE");
        }

        // Valida se há usuários ativos vinculados (vinculação direta)
        long usuariosAtivos = profileRepository.countUserActive(command.code());
        if (usuariosAtivos > 0) {
            log.warn("Tentativa de inativar perfil com usuários ativos. Código: {}, Usuários: {}",
                command.code(), usuariosAtivos);
            throw new BusinessException(
                "Não é possível inativar o perfil '" + command.code()
                    + "': " + usuariosAtivos + " usuário(s) ativo(s) vinculado(s).",
                "HAS_ACTIVE_USERS");
        }

        profileRepository.updateStatus(
            command.code(), "I", command.idUsuarioLogado());
        
        log.info("Perfil desativado. Código: {}", command.code());
    }

    /**
     * Reativa perfil inativo.
     * 
     * @param command contém código do perfil
     * @throws BusinessException se perfil já está ativo
     */
    public void reactivateProfile(GroupProfileManagementUseCase.ReactivateCommand command) {
        log.info("Reativando perfil. Código: {}", command.code());
        
        Profile perfil = findProfileOrThrow(command.code());

        if (perfil.isActive()) {
            log.warn("Perfil já está ativo. Código: {}", command.code());
            throw new BusinessException(
                "O perfil '" + command.code() + "' já está ativo.",
                "ALREADY_ACTIVE");
        }

        profileRepository.updateStatus(
            command.code(), "A", command.idUsuarioLogado());
        
        log.info("Perfil reativado. Código: {}", command.code());
    }

    /**
     * Associa funcionalidades ao perfil.
     * 
     * @param command contém código do perfil e lista de funcionalidades
     * @throws DuplicateResourceException se funcionalidade já associada
     */
    @CacheEvict(value = "permissoes", allEntries = true)
    public void associateFunctionalitiesToProfile(
        GroupProfileManagementUseCase.AssociateFunctionalitiesToProfileCommand command) {
        
        log.info("Associando funcionalidades ao perfil. Código: {}", command.codPerfil());
        
        findProfileOrThrow(command.codPerfil());

        for (Functionality functionality : command.functionalities()) {
            // Valida se já está associada
            if (profileRepository.isFunctionalityAssociate(
                command.codPerfil(), 
                functionality.getId())) {
                
                log.warn("Funcionalidade já associada ao perfil. Perfil: {}, Funcionalidade: {}",
                    command.codPerfil(), functionality.canonicalKey());
                throw new DuplicateResourceException(
                    "Funcionalidade já está associada ao perfil.");
            }

            profileRepository.associateFunctionalitiesToProfile(
                command.codPerfil(), 
                functionality.getId(), 
                command.idUsuarioLogado());
            
            log.info("Funcionalidade associada. Perfil: {}, Funcionalidade: {}",
                command.codPerfil(), functionality.canonicalKey());
        }
    }

    /**
     * Remove associação de funcionalidade do perfil.
     * 
     * @param command contém código do perfil e funcionalidade
     */
    @CacheEvict(value = "permissoes", allEntries = true)
    public void disassociateFunctionalityFromProfile(
        GroupProfileManagementUseCase.DisassociateFunctionalityFromProfileCommand command) {
        
        log.info("Removendo funcionalidade do perfil. Perfil: {}, Funcionalidade: {}",
            command.codPerfil(), command.functionality().codFuncionalidade());
        
        findProfileOrThrow(command.codPerfil());

        var functionalityKey = new FunctionalityKey(
            command.functionality().codSistema(),
            command.functionality().codModulo(),
            command.functionality().codRotina(),
            command.functionality().codFuncionalidade());

        profileRepository.desassociateFunctionalitiesToProfile(
            command.codPerfil(), 
            functionalityKey, 
            command.idUsuarioLogado());
        
        log.info("Funcionalidade removida. Perfil: {}, Funcionalidade: {}",
            command.codPerfil(), command.functionality().codFuncionalidade());
    }

    /**
     * Lista todos os perfis, opcionalmente filtrado por status.
     * 
     * @param statusCode status de filtro (null = todos)
     * @return lista de perfis
     */
    public List<Profile> listProfiles(String statusCode) {
        log.debug("Listando perfis. Status: {}", statusCode);
        return profileRepository.listProfile(statusCode);
    }

    // ── Utilitários privados ──────────────────────────────────────────────────
    private Group findGroupOrThrow(String codGrupo) {
        return groupRepository.findById(codGrupo)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Grupo", "codGrupo", codGrupo));
    }

    private Profile findProfileOrThrow(String codPerfil) {
        return profileRepository.findById(codPerfil)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Perfil", "codPerfil", codPerfil));
    }
}
