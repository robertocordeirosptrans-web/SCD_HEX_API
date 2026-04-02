package br.sptrans.scd.auth.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.application.port.out.GroupProfileRepository;
import br.sptrans.scd.auth.application.usecases.grouporprofile.ManageGroupProfileUseCase;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.GroupProfile;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.ProfileFunctionality;
import br.sptrans.scd.auth.domain.UserProfile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ManageProfileGroupService implements GroupProfileManagementUseCase {

    private final ManageGroupProfileUseCase manageGroupProfileUseCase;
    private final GroupProfileRepository groupProfileRepository;

    // ══════════════════════════════════════════════════════════════════════════
    // GROUP PROFILE — CRUD direto
    // ══════════════════════════════════════════════════════════════════════════
    public List<GroupProfile> findAllGroupProfile() {
        return groupProfileRepository.findAllGroupProfile();
    }

    public Optional<GroupProfile> findByCodGrupoAndCodPerfil(String codGrupo, String codPerfil) {
        return groupProfileRepository.findByCodGrupoAndCodPerfil(codGrupo, codPerfil);
    }

    public GroupProfile saveGroupProfile(GroupProfile groupProfile) {
        return groupProfileRepository.saveGroupProfile(groupProfile);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GRUPOS
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public Group createGroup(CreateGroupCommand cmd) {
        return manageGroupProfileUseCase.createGroup(cmd);
    }

    @Override
    public Group updateGroup(UpdateGroupCommand cmd) {
        return manageGroupProfileUseCase.updateGroup(cmd);
    }

    @Override
    public void deactivateGroup(DeactivateCommand cmd) {
        manageGroupProfileUseCase.deactivateGroup(cmd);
    }

    @Override
    public void reactivateGroup(ReactivateCommand cmd) {
        manageGroupProfileUseCase.reactivateGroup(cmd);
    }

    @Override
    public void associateProfilesToGroup(AssociateProfilesToGroupCommand cmd) {
        manageGroupProfileUseCase.associateProfilesToGroup(cmd);
    }

    @Override
    public void disassociateProfileFromGroup(DisassociateProfileFromGroupCommand cmd) {
        manageGroupProfileUseCase.disassociateProfileFromGroup(cmd);
    }

    @Override
    public List<Functionality> listFunctionalities() {
        return manageGroupProfileUseCase.listFunctionalities();
    }

    @Override
    public List<Group> listGroups(String statusCode) {
        return manageGroupProfileUseCase.listGroups(statusCode);
    }

    @Override
    public List<GroupUser> listGroupUsersByCodGrupo(String codGrupo) {
        return manageGroupProfileUseCase.listGroupUsersByCodGrupo(codGrupo);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PERFIS
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public Profile createProfile(CreateProfileCommand cmd) {
        return manageGroupProfileUseCase.createProfile(cmd);
    }

    @Override
    public Profile updateProfile(UpdateProfileCommand cmd) {
        return manageGroupProfileUseCase.updateProfile(cmd);
    }

    @Override
    public void deactivateProfile(DeactivateCommand cmd) {
        manageGroupProfileUseCase.deactivateProfile(cmd);
    }

    @Override
    public void reactivateProfile(ReactivateCommand cmd) {
        manageGroupProfileUseCase.reactivateProfile(cmd);
    }

    @Override
    public void associateFunctionalitiesToProfile(AssociateFunctionalitiesToProfileCommand cmd) {
        manageGroupProfileUseCase.associateFunctionalitiesToProfile(cmd);
    }

    @Override
    public void disassociateFunctionalityFromProfile(DisassociateFunctionalityFromProfileCommand cmd) {
        manageGroupProfileUseCase.disassociateFunctionalityFromProfile(cmd);
    }

    @Override
    public List<Profile> listProfiles(String statusCode) {
        return manageGroupProfileUseCase.listProfiles(statusCode);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MÉTODOS LEGADOS (Compatibilidade)
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public List<GroupUser> listGroupUsers() {
        return null;
    }

    @Override
    public List<UserProfile> listUserProfiles() {
        return null;
    }

    @Override
    public List<ProfileFunctionality> listProfileFunctionalities() {
        return null;
    }
}
