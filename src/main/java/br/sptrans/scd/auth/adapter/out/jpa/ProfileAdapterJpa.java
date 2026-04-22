package br.sptrans.scd.auth.adapter.out.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileFunctionalityProjectionDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserProfileProjectionDTO;
import br.sptrans.scd.auth.adapter.out.jpa.mapper.FunctionalityMapper;
import br.sptrans.scd.auth.adapter.out.jpa.mapper.ProfileMapper;
import br.sptrans.scd.auth.adapter.out.jpa.repository.FunctionalityJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.ProfileFunctionalityJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.ProfileJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.UserProfileJpaRepository;
import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpaKey;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileFunctionalityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileFunctionalityJpaId;
import br.sptrans.scd.auth.application.port.out.ProfilePort;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.ProfileFunctionality;
import br.sptrans.scd.auth.domain.ProfileFunctionalityKey;
import br.sptrans.scd.auth.domain.UserProfile;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProfileAdapterJpa implements ProfilePort {

    private final ProfileJpaRepository profileJpaRepository;
    private final ProfileFunctionalityJpaRepository profileFunctionalityJpaRepository;
    private final FunctionalityJpaRepository functionalityJpaRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final ProfileMapper profileMapper;
    private final FunctionalityMapper functionalityMapper;

    @Override
    public Optional<Profile> findById(String codPerfil) {
        return profileJpaRepository.findByCodPerfil(codPerfil).map(profileMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String codPerfil) {
        return profileJpaRepository.countByCodPerfil(codPerfil) > 0;
    }

    @Override
    public List<Profile> listProfile(String codStatus) {
        return profileJpaRepository.findByCodStatus(codStatus).stream()
                .map(profileMapper::toDomain).toList();
    }

    @Override
    public Page<Profile> listProfile(String nomPerfil, String codStatus, Pageable pageable) {
        return profileJpaRepository.findByNomPerfilAndCodStatus(nomPerfil, codStatus, pageable).map(profileMapper::toDomain);
    }


    @Override
    public void updateStatus(String codPerfil, String codStatus, Long idUsuarioManutencao) {
        profileJpaRepository.findByCodPerfil(codPerfil).ifPresent(entity -> {
            entity.setCodStatus(codStatus);
            entity.setIdUsuarioManutencao(idUsuarioManutencao);
            entity.setDtManutencao(java.time.LocalDateTime.now());
            profileJpaRepository.save(entity);
        });
    }

    @Override
    public void associateFunctionalitiesToProfile(String codPerfil, FunctionalityKey chave, Long idUsuarioManutencao) {
        ProfileFunctionalityJpaId pfId = profileMapper.toJpaId(new ProfileFunctionalityKey(
                chave.getCodSistema(), chave.getCodModulo(), chave.getCodRotina(),
                chave.getCodFuncionalidade(), codPerfil));
        ProfileFunctionalityJpa pf = new ProfileFunctionalityJpa();
        pf.setId(pfId);
        pf.setIdUsuarioManutencao(idUsuarioManutencao);
        profileFunctionalityJpaRepository.save(pf);
    }

    @Override
    public void desassociateFunctionalitiesToProfile(String codPerfil, FunctionalityKey chave,
            Long idUsuarioManutencao) {
        profileFunctionalityJpaRepository.desassociateFunctionality(
                codPerfil,
                chave.getCodSistema(),
                chave.getCodModulo(),
                chave.getCodRotina(),
                chave.getCodFuncionalidade(),
                idUsuarioManutencao);
    }

    @Override
    public boolean isFunctionalityAssociate(String codPerfil, FunctionalityKey chave) {
        return profileFunctionalityJpaRepository.countFunctionalityAssociation(
                codPerfil,
                chave.getCodSistema(),
                chave.getCodModulo(),
                chave.getCodRotina(),
                chave.getCodFuncionalidade()) > 0;
    }

    @Override
    public List<Functionality> listFunctionalityActive() {
        return functionalityJpaRepository.findAll().stream()
                .filter(f -> "A".equalsIgnoreCase(f.getCodStatus()))
                .map(functionalityMapper::toDomain)
                .toList();
    }

    public Page<ProfileFunctionalityProjectionDTO> listFunctionalitiesProjectionByProfile(
            String codPerfil, Pageable pageable) {
        return profileFunctionalityJpaRepository.findAllProjectedByCodPerfil(codPerfil, pageable);
    }

    @Override
    public Page<UserProfileProjectionDTO> listUserProfilesByPerfil(
            String codPerfil, Pageable pageable) {
        return userProfileJpaRepository.findAllProjectedByCodPerfil(codPerfil, pageable);
    }

    @Override
    public boolean isFunctionality(FunctionalityKey chave) {
        FunctionalityEntityJpaKey key = new FunctionalityEntityJpaKey();
        key.setCodSistema(chave.getCodSistema());
        key.setCodModulo(chave.getCodModulo());
        key.setCodRotina(chave.getCodRotina());
        key.setCodFuncionalidade(chave.getCodFuncionalidade());
        return functionalityJpaRepository.findById(key)
                .map(f -> "A".equalsIgnoreCase(f.getCodStatus()))
                .orElse(false);
    }

    @Override
    public long countUserActive(String codPerfil) {
        return userProfileJpaRepository.countActiveUsersByProfile(codPerfil);
    }

    @Override
    public List<UserProfile> listUserProfiles() {
        return userProfileJpaRepository.listAllUserProfiles().stream()
                .map(profileMapper::toDomain)
                .toList();
    }

    @Override
    public Page<UserProfile> listUserProfiles(Pageable pageable) {
        return userProfileJpaRepository.listAllUserProfiles(pageable).map(profileMapper::toDomain);
    }


    @Override
    public void save(Profile perfil) {
        profileJpaRepository.save(profileMapper.toEntity(perfil));
    }

    @Override
    public List<ProfileFunctionality> listProfileFunctionalities() {
        return profileFunctionalityJpaRepository.findAll().stream()
                .map(profileMapper::toDomain)
                .toList();
    }

    @Override
    public Page<ProfileFunctionality> listProfileFunctionalities(Pageable pageable) {
        return profileFunctionalityJpaRepository.findAll(pageable).map(profileMapper::toDomain);
    }

    @Override
    public Optional<ProfileFunctionality> findById(ProfileFunctionalityKey id) {
        return profileFunctionalityJpaRepository.findById(profileMapper.toJpaId(id))
                .map(profileMapper::toDomain);
    }

    @Override
    public List<ProfileFunctionality> findAll() {
        return profileFunctionalityJpaRepository.findAll().stream()
                .map(profileMapper::toDomain)
                .toList();
    }

    @Override
    public ProfileFunctionality save(ProfileFunctionality entity) {
        ProfileFunctionalityJpa saved = profileFunctionalityJpaRepository.save(profileMapper.toEntity(entity));
        return profileMapper.toDomain(saved);
    }

    @Override
    public void delete(ProfileFunctionality entity) {
        profileFunctionalityJpaRepository.delete(profileMapper.toEntity(entity));
    }

    @Override
    public void deleteById(ProfileFunctionalityKey id) {
        profileFunctionalityJpaRepository.deleteById(profileMapper.toJpaId(id));
    }

    @Override
    public long count() {
        return profileFunctionalityJpaRepository.count();
    }
}
