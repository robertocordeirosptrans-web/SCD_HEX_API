package br.sptrans.scd.auth.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.application.port.out.GroupRepository;
import br.sptrans.scd.auth.application.port.out.ProfileRepository;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.ProfileFunctionality;
import br.sptrans.scd.auth.domain.UserProfile;
import br.sptrans.scd.shared.exception.BusinessException;
import br.sptrans.scd.shared.exception.DuplicateResourceException;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ManageProfileGroupService implements GroupProfileManagementUseCase {

    private final GroupRepository groupRepository;
    private final ProfileRepository profileRepository;

    public ManageProfileGroupService(GroupRepository groupRepository,
            ProfileRepository profileRepository) {
        this.groupRepository = groupRepository;
        this.profileRepository = profileRepository;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GRUPOS
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public Group createGroup(CreateGroupCommand cmd) {
        if (groupRepository.existsByCode(cmd.codGrupo())) {
            throw new DuplicateResourceException("Grupo", "codGrupo", cmd.codGrupo());
        }

        Group grupo = new Group();
        grupo.setCodGrupo(cmd.codGrupo().toUpperCase().trim());
        grupo.setNomGrupo(cmd.nomGrupo().trim());
        grupo.setCodStatus("A");

        groupRepository.save(grupo);
        return grupo;
    }

    @Override
    public Group updateGroup(UpdateGroupCommand cmd) {
        Group grupo = buscarGrupoOuLancar(cmd.codGrupo());

        grupo.setNomGrupo(cmd.nomGrupo().trim());

        groupRepository.save(grupo);
        return grupo;
    }

    @Override
    public void deactivateGroup(DeactivateCommand cmd) {
        Group grupo = buscarGrupoOuLancar(cmd.code());

        if (!grupo.isActive()) {
            throw new BusinessException("O grupo '" + cmd.code() + "' já está inativo.", "ALREADY_INACTIVE");
        }

        long usuariosAtivos = groupRepository.countUserActive(cmd.code());
        if (usuariosAtivos > 0) {
            throw new BusinessException(
                    "Não é possível inativar o grupo '" + cmd.code() + "': "
                    + usuariosAtivos + " usuário(s) ativo(s) vinculado(s).", "HAS_ACTIVE_USERS");
        }

        groupRepository.updateStatus(cmd.code(), "I", cmd.idUsuarioLogado());
    }

    @Override
    public void reactivateGroup(ReactivateCommand cmd) {
        Group grupo = buscarGrupoOuLancar(cmd.code());

        if (grupo.isActive()) {
            throw new BusinessException("O grupo '" + cmd.code() + "' já está ativo.", "ALREADY_ACTIVE");
        }

        groupRepository.updateStatus(cmd.code(), "A", cmd.idUsuarioLogado());
    }

    @Override
    public void associateProfilesToGroup(AssociateProfilesToGroupCommand cmd) {
        buscarGrupoOuLancar(cmd.groupCode());

        for (String codPerfil : cmd.profileCodes()) {
            // Valida existência do perfil
            profileRepository.findById(codPerfil)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil", "codPerfil", codPerfil));

            if (groupRepository.isProfileAssociate(cmd.groupCode(), codPerfil)) {
                throw new DuplicateResourceException("Perfil '" + codPerfil + "' já está associado ao grupo '" + cmd.groupCode() + "'.");
            }

            groupRepository.associateProfilesToGroup(cmd.groupCode(), codPerfil, cmd.idUsuarioLogado());
        }
    }

    // ── Helpers privados ─────────────────────────────────────────────────────
    private Group buscarGrupoOuLancar(String codGrupo) {
        return groupRepository.findById(codGrupo)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo", "codGrupo", codGrupo));
    }

    private Profile buscarPerfilOuLancar(String codPerfil) {
        return profileRepository.findById(codPerfil)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil", "codPerfil", codPerfil));
    }

    // Conversão de FunctionalityKey do caso de uso para o domínio
    // private FunctionalityKey toDomainKey(GroupProfileManagementUseCase.FunctionalityKey key) {
    //     return new FunctionalityKey(
    //         key.codSistema(),
    //         key.codModulo(),
    //         key.codRotina(),
    //         key.codFuncionalidade()
    //     );
    // }

    // ══════════════════════════════════════════════════════════════════════════
    // PERFIS
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public Profile createProfile(CreateProfileCommand cmd) {
        if (profileRepository.existsByCode(cmd.codPerfil())) {
            throw new DuplicateResourceException("Perfil", "codPerfil", cmd.codPerfil());
        }
        Profile perfil = new Profile();
        perfil.setCodPerfil(cmd.codPerfil().toUpperCase().trim());
        perfil.setNomPerfil(cmd.nomPerfil().trim());
        perfil.setCodStatus("A");
        perfil.setIdUsuarioManutencao(cmd.idUsuarioLogado());
        profileRepository.save(perfil);
        return perfil;
    }

    @Override
    public Profile updateProfile(UpdateProfileCommand cmd) {
        Profile perfil = buscarPerfilOuLancar(cmd.codPerfil());
        perfil.setNomPerfil(cmd.nomPerfil().trim());
        perfil.setIdUsuarioManutencao(cmd.idUsuarioLogado());
        profileRepository.save(perfil);
        return perfil;
    }

    @Override
    public void deactivateProfile(DeactivateCommand cmd) {
        Profile perfil = buscarPerfilOuLancar(cmd.code());
        if (!perfil.isActive()) {
            throw new BusinessException("O perfil '" + cmd.code() + "' já está inativo.", "ALREADY_INACTIVE");
        }
        long usuariosAtivos = profileRepository.countUserActive(cmd.code());
        if (usuariosAtivos > 0) {
            throw new BusinessException(
                    "Não é possível inativar o perfil '" + cmd.code() + "': "
                    + usuariosAtivos + " usuário(s) ativo(s) vinculado(s).", "HAS_ACTIVE_USERS");
        }
        profileRepository.updateStatus(cmd.code(), "I", cmd.idUsuarioLogado());
    }

    @Override
    public void reactivateProfile(ReactivateCommand cmd) {
        Profile perfil = buscarPerfilOuLancar(cmd.code());
        if (perfil.isActive()) {
            throw new BusinessException("O perfil '" + cmd.code() + "' já está ativo.", "ALREADY_ACTIVE");
        }
        profileRepository.updateStatus(cmd.code(), "A", cmd.idUsuarioLogado());
    }

    @Override
    public void associateFunctionalitiesToProfile(AssociateFunctionalitiesToProfileCommand cmd) {
        buscarPerfilOuLancar(cmd.codPerfil());
        for (var func : cmd.functionalities()) {
            if (!profileRepository.isFunctionality(func.getId())) {
                throw new ResourceNotFoundException("Funcionalidade", "id", func.canonicalKey());
            }
            if (profileRepository.isFunctionalityAssociate(cmd.codPerfil(), func.getId())) {
                throw new DuplicateResourceException("Funcionalidade '" + func.canonicalKey() + "' já está associada ao perfil '" + cmd.codPerfil() + "'.");
            }
            profileRepository.associateFunctionalitiesToProfile(cmd.codPerfil(), func.getId(), cmd.idUsuarioLogado());
        }
    }

    @Override
    public void disassociateFunctionalityFromProfile(DisassociateFunctionalityFromProfileCommand cmd) {
        // buscarPerfilOuLancar(cmd.codPerfil());
        // FunctionalityKey domainKey = toDomainKey(cmd.functionality());
        // if (!profileRepository.isFunctionalityAssociate(cmd.codPerfil(), domainKey)) {
        //     throw new GroupProfileManagementException(
        //             ErrorType.FUNCTIONALITY_NOT_FOUND,
        //             "Funcionalidade não está associada ao perfil: '" + cmd.functionality().codFuncionalidade() + "'.");
        // }
        // profileRepository.desassociateFunctionalitiesToProfile(cmd.codPerfil(), domainKey, cmd.idUsuarioLogado());
    }

    @Override
    public List<Profile> listProfiles(String statusCode) {
        return profileRepository.listProfile(statusCode);
    }

    @Override
    public List<Functionality> listFunctionalities() {
        return profileRepository.listFunctionalityActive();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GRUPOS (complementares)
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public void disassociateProfileFromGroup(DisassociateProfileFromGroupCommand cmd) {
        buscarGrupoOuLancar(cmd.groupCode());
        buscarPerfilOuLancar(cmd.profileCode());
        if (!groupRepository.isProfileAssociate(cmd.groupCode(), cmd.profileCode())) {
            throw new ResourceNotFoundException("Perfil '" + cmd.profileCode() + "' não está associado ao grupo '" + cmd.groupCode() + "'.");
        }
        groupRepository.disassociateProfileFromGroup(cmd.groupCode(), cmd.profileCode(), cmd.idUsuarioLogado());
    }

    @Override
    public List<Group> listGroups(String statusCode) {
        return groupRepository.listGroups(statusCode);
    }

    @Override
    public List<GroupUser> listGroupUsers() {
        return groupRepository.listGroupUsers();
    }

    @Override
    public List<UserProfile> listUserProfiles() {
        return profileRepository.listUserProfiles();
    }

    @Override
    public List<ProfileFunctionality> listProfileFunctionalities() {
        return profileRepository.listProfileFunctionalities();
    }
}
