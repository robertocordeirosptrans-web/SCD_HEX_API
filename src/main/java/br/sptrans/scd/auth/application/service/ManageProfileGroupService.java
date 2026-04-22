
package br.sptrans.scd.auth.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileFunctionalityProjectionDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileProjectionDTO;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupCustomProjection;
import br.sptrans.scd.auth.adapter.out.jpa.repository.GroupUserCustomProjection;
import br.sptrans.scd.auth.application.port.in.GroupProfileManagementUseCase;
import br.sptrans.scd.auth.application.port.out.GroupProfilePort;
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
    private final GroupProfilePort groupProfileRepository;

    // ══════════════════════════════════════════════════════════════════════════
    // GROUP PROFILE — CRUD direto
    // ══════════════════════════════════════════════════════════════════════════
    public List<GroupProfile> findAllGroupProfile() {
        return groupProfileRepository.findAllGroupProfile();
    }

    public Page<GroupProfile> findAllGroupProfile(Pageable pageable) {
        return groupProfileRepository.findAllGroupProfile(pageable);
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
    public Page<Group> listGroups(String nomGrupo, String codStatus, Pageable pageable) {
        return manageGroupProfileUseCase.listGroups(nomGrupo, codStatus, pageable);
    }

    @Override
    public Page<ProfileFunctionalityProjectionDTO> listFunctionalitiesProjectionByProfile(
            String codPerfil, Pageable pageable) {
        return manageGroupProfileUseCase.listFunctionalitiesProjectionByProfile(codPerfil, pageable);
    }

    @Override
    public Page<GroupUserCustomProjection> listCustomUsersByGroup(String codGrupo, Pageable pageable) {
        return manageGroupProfileUseCase.listCustomUsersByGroup(codGrupo, pageable);
    }

    public Page<GroupCustomProjection> listCustomGroupsByUser(Long idUsuario, Pageable pageable) {
        return manageGroupProfileUseCase.listCustomGroupsByUser(idUsuario, pageable);
    }

    @Override
    public Optional<Group> getGroupByCode(String codGrupo) {
        return manageGroupProfileUseCase.getGroupByCode(codGrupo);
    }

    @Override
    public Optional<Group> findById(String codGrupo) {
        return manageGroupProfileUseCase.findById(codGrupo);
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

    @Override
    public Page<Profile> listProfiles(String nomPerfil, String statusCode, Pageable pageable) {
        return manageGroupProfileUseCase.listProfiles(nomPerfil, statusCode, pageable);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MÉTODOS LEGADOS (Compatibilidade)
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    public List<UserProfile> listUserProfiles() {
        return manageGroupProfileUseCase.listUserProfiles();
    }

    @Override
    public List<GroupUser> listGroupUsers() {
        return manageGroupProfileUseCase.listGroupUsers();
    }

    @Override
    public Page<GroupUser> listGroupUsers(Pageable pageable) {
        return manageGroupProfileUseCase.listGroupUsers(pageable);
    }

    public Page<UserProfile> listUserProfiles(Pageable pageable) {
        return manageGroupProfileUseCase.listUserProfiles(pageable);
    }

    @Override
    public Page<UserProfileProjectionDTO> listUserProfilesByPerfil(String codPerfil, Pageable pageable) {
        return manageGroupProfileUseCase.listUserProfilesByPerfil(codPerfil, pageable);
    }

    @Override
    public List<ProfileFunctionality> listProfileFunctionalities() {
        return manageGroupProfileUseCase.listProfileFunctionalities();
    }

    public Page<ProfileFunctionality> listProfileFunctionalities(Pageable pageable) {
        return manageGroupProfileUseCase.listProfileFunctionalities(pageable);
    }

}
