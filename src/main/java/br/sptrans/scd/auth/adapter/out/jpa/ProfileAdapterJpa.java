package br.sptrans.scd.auth.adapter.out.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.jpa.repository.ProfileJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.ProfileFunctionalityJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.FunctionalityJpaRepository;
import br.sptrans.scd.auth.adapter.out.jpa.repository.UserProfileJpaRepository;
import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpaKey;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileFunctionalityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileFunctionalityJpaId;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserProfileJpa;
import br.sptrans.scd.auth.application.port.out.ProfilePort;

import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.ProfileFunctionality;
import br.sptrans.scd.auth.domain.ProfileFunctionalityKey;
import br.sptrans.scd.auth.domain.UserProfile;
import br.sptrans.scd.auth.domain.UserProfileId;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProfileAdapterJpa implements ProfilePort {

    private final ProfileJpaRepository profileJpaRepository;
    private final ProfileFunctionalityJpaRepository profileFunctionalityJpaRepository;
    private final FunctionalityJpaRepository functionalityJpaRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;

    @Override

    public Optional<Profile> findById(String codPerfil) {
        return profileJpaRepository.findByCodPerfil(codPerfil).map(this::toDomainProfile);
    }

    @Override
    public boolean existsByCode(String codPerfil) {
        return profileJpaRepository.countByCodPerfil(codPerfil) > 0;
    }

    @Override
    public List<Profile> listProfile(String codStatus) {
        return profileJpaRepository.findByCodStatus(codStatus).stream().map(this::toDomainProfile).toList();
    }

    @Override
    public Page<Profile> listProfile(String codStatus, Pageable pageable) {
        return profileJpaRepository.findByCodStatus(codStatus, pageable).map(this::toDomainProfile);
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
        ProfileFunctionalityJpaId pfId = new ProfileFunctionalityJpaId();
        pfId.setCodPerfil(codPerfil);
        pfId.setCodSistema(chave.getCodSistema());
        pfId.setCodModulo(chave.getCodModulo());
        pfId.setCodRotina(chave.getCodRotina());
        pfId.setCodFuncionalidade(chave.getCodFuncionalidade());
        ProfileFunctionalityJpa pf = new ProfileFunctionalityJpa();
        pf.setId(pfId);
        pf.setIdUsuarioManutencao(idUsuarioManutencao);
        profileFunctionalityJpaRepository.save(pf);
    }

    @Override
    public void desassociateFunctionalitiesToProfile(String codPerfil, FunctionalityKey chave, Long idUsuarioManutencao) {
        profileFunctionalityJpaRepository.desassociateFunctionality(
                codPerfil,
                chave.getCodSistema(),
                chave.getCodModulo(),
                chave.getCodRotina(),
                chave.getCodFuncionalidade(),
                idUsuarioManutencao
        );
    }

    @Override
    public boolean isFunctionalityAssociate(String codPerfil, FunctionalityKey chave) {
        return profileFunctionalityJpaRepository.countFunctionalityAssociation(
                codPerfil,
                chave.getCodSistema(),
                chave.getCodModulo(),
                chave.getCodRotina(),
                chave.getCodFuncionalidade()
        ) > 0;
    }

    @Override
    public List<Functionality> listFunctionalityActive() {
        return functionalityJpaRepository.findAll().stream()
                .filter(f -> "A".equalsIgnoreCase(f.getCodStatus()))
                .map(this::toDomainFunctionality)
                .toList();
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
                .map(this::toDomainUserProfile)
                .toList();
    }

    @Override
    public Page<UserProfile> listUserProfiles(Pageable pageable) {
        return userProfileJpaRepository.listAllUserProfiles(pageable).map(this::toDomainUserProfile);
    }

    @Override
    public Page<UserProfile> listUserProfilesByPerfil(String codPerfil, Pageable pageable) {
        return userProfileJpaRepository.findByIdCodPerfil(codPerfil, pageable).map(this::toDomainUserProfile);
    }

    @Override
    public void save(Profile perfil) {
        profileJpaRepository.save(toEntityProfile(perfil));
    }
    private Profile toDomainProfile(ProfileEntityJpa entity) {
        Profile p = new Profile();
        p.setCodPerfil(entity.getCodPerfil());
        p.setNomPerfil(entity.getNomPerfil());
        p.setCodStatus(entity.getCodStatus());
        p.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        p.setDtModi(entity.getDtManutencao());
        return p;
    }

    private ProfileEntityJpa toEntityProfile(Profile domain) {
        ProfileEntityJpa e = new ProfileEntityJpa();
        e.setCodPerfil(domain.getCodPerfil());
        e.setNomPerfil(domain.getNomPerfil());
        e.setCodStatus(domain.getCodStatus());
        e.setIdUsuarioManutencao(domain.getIdUsuarioManutencao());
        e.setDtManutencao(domain.getDtModi());
        return e;
    }

    @Override
    public List<ProfileFunctionality> listProfileFunctionalities() {
        return profileFunctionalityJpaRepository.findAll().stream()
                .map(this::toDomainProfileFunctionality)
                .toList();
    }

    @Override
    public Page<ProfileFunctionality> listProfileFunctionalities(Pageable pageable) {
        return profileFunctionalityJpaRepository.findAll(pageable).map(this::toDomainProfileFunctionality);
    }

    private ProfileFunctionality toDomainProfileFunctionality(ProfileFunctionalityJpa entity) {
        ProfileFunctionalityKey key = new ProfileFunctionalityKey(
                entity.getId().getCodSistema(),
                entity.getId().getCodModulo(),
                entity.getId().getCodRotina(),
                entity.getId().getCodFuncionalidade(),
                entity.getId().getCodPerfil()
        );
        ProfileFunctionality pf = new ProfileFunctionality();
        pf.setId(key);
        pf.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        if (entity.getDtInicioValidade() != null) {
            pf.setDtInicioValidade(entity.getDtInicioValidade().toLocalDate());
        }
        // pf.setFuncionalidade(...); // Se necessário, mapear funcionalidade
        // pf.setPerfil(...); // Se necessário, mapear perfil
        return pf;
    }

    // Métodos auxiliares de conversão
    private Functionality toDomainFunctionality(FunctionalityEntityJpa entity) {
        FunctionalityKey key = new FunctionalityKey(
                entity.getId().getCodSistema(),
                entity.getId().getCodModulo(),
                entity.getId().getCodRotina(),
                entity.getId().getCodFuncionalidade()
        );
        Functionality f = new Functionality();
        f.setId(key);
        f.setCodSistema(entity.getId().getCodSistema());
        f.setCodModulo(entity.getId().getCodModulo());
        f.setCodRotina(entity.getId().getCodRotina());
        f.setCodFuncionalidade(entity.getId().getCodFuncionalidade());
        f.setNomFuncionalidade(entity.getNomFuncionalidade());
        f.setCodStatus(entity.getCodStatus());
        f.setDtModi(entity.getDtManutencao());
        return f;
    }

    private UserProfile toDomainUserProfile(UserProfileJpa entity) {
        UserProfileId id = new UserProfileId();
        id.setIdUsuario(entity.getId().getIdUsuario());
        id.setCodPerfil(entity.getId().getCodPerfil());
        id.setDtInicioValidade(entity.getId().getDtInicioValidade());
        id.setDtFimValidade(entity.getId().getDtFimValidade());
        UserProfile up = new UserProfile();
        up.setId(id);
        up.setCodStatus(entity.getCodStatus());
        up.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        up.setDtModi(entity.getDtManutencao());
        // up.setUser(...); // Se necessário, mapear usuário
        // up.setPerfil(...); // Se necessário, mapear perfil
        return up;
    }

 

    @Override
    public Optional<ProfileFunctionality> findById(ProfileFunctionalityKey id) {
        ProfileFunctionalityJpaId jpaId = toJpaId(id);
        return profileFunctionalityJpaRepository.findById(jpaId)
                .map(this::toDomainProfileFunctionality);
    }

    @Override
    public List<ProfileFunctionality> findAll() {
        return profileFunctionalityJpaRepository.findAll().stream()
                .map(this::toDomainProfileFunctionality)
                .toList();
    }

    @Override
    public ProfileFunctionality save(ProfileFunctionality entity) {
        ProfileFunctionalityJpa jpaEntity = toEntityProfileFunctionality(entity);
        ProfileFunctionalityJpa saved = profileFunctionalityJpaRepository.save(jpaEntity);
        return toDomainProfileFunctionality(saved);
    }

    @Override
    public void delete(ProfileFunctionality entity) {
        ProfileFunctionalityJpa jpaEntity = toEntityProfileFunctionality(entity);
        profileFunctionalityJpaRepository.delete(jpaEntity);
    }

    @Override
    public void deleteById(ProfileFunctionalityKey id) {
        profileFunctionalityJpaRepository.deleteById(toJpaId(id));
    }

    @Override
    public long count() {
        return profileFunctionalityJpaRepository.count();
    }

    private ProfileFunctionalityJpaId toJpaId(ProfileFunctionalityKey id) {
        ProfileFunctionalityJpaId jpaId = new ProfileFunctionalityJpaId();
        jpaId.setCodPerfil(id.getCodPerfil());
        jpaId.setCodSistema(id.getCodSistema());
        jpaId.setCodModulo(id.getCodModulo());
        jpaId.setCodRotina(id.getCodRotina());
        jpaId.setCodFuncionalidade(id.getCodFuncionalidade());
        return jpaId;
    }

    private ProfileFunctionalityJpa toEntityProfileFunctionality(ProfileFunctionality domain) {
        ProfileFunctionalityJpaId jpaId = toJpaId(domain.getId());
        ProfileFunctionalityJpa entity = new ProfileFunctionalityJpa();
        entity.setId(jpaId);
        entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao());
        if (domain.getDtInicioValidade() != null) {
            entity.setDtInicioValidade(domain.getDtInicioValidade().atStartOfDay());
        }
        return entity;
    }

}
