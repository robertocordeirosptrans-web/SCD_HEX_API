package br.sptrans.scd.auth.adapter.out.jpa;

import java.time.LocalDateTime;
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
import br.sptrans.scd.auth.adapter.out.jpa.repository.UserRepositoryJpa;
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
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProfileAdapterJpa implements ProfilePort {

    private final ProfileJpaRepository profileJpaRepository;
    private final UserRepositoryJpa userRepositoryJpa;
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
        return profileJpaRepository.findByNomPerfilAndCodStatus(nomPerfil, codStatus, pageable)
                .map(profileMapper::toDomain);
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
        // Buscar as entidades relacionadas para estabelecer as relações JPA
        var perfil = profileJpaRepository.findByCodPerfil(codPerfil)
                .orElseThrow(() -> new IllegalArgumentException("Perfil não encontrado: " + codPerfil));

        FunctionalityEntityJpaKey functionalityKey = new FunctionalityEntityJpaKey();
        functionalityKey.setCodSistema(chave.getCodSistema());
        functionalityKey.setCodModulo(chave.getCodModulo());
        functionalityKey.setCodRotina(chave.getCodRotina());
        functionalityKey.setCodFuncionalidade(chave.getCodFuncionalidade());

        var funcionalidade = functionalityJpaRepository.findById(functionalityKey)
                .orElseThrow(() -> new IllegalArgumentException("Funcionalidade não encontrada: " + functionalityKey));

        ProfileFunctionalityJpaId pfId = profileMapper.toJpaId(new ProfileFunctionalityKey(
                chave.getCodSistema(), chave.getCodModulo(), chave.getCodRotina(),
                chave.getCodFuncionalidade(), codPerfil));

        ProfileFunctionalityJpa pf = new ProfileFunctionalityJpa();
        pf.setId(pfId);
        pf.setIdUsuarioManutencao(idUsuarioManutencao);
        pf.setPerfil(perfil); // ✅ Setar a relação com Perfil
        pf.setFuncionalidade(funcionalidade); // ✅ Setar a relação com Funcionalidade
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

    @Override
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
    public Page<UserProfile> findByIdUsuario(Long idUsuario, Pageable pageable) {
        return userProfileJpaRepository.findByIdUsuario(idUsuario, pageable).map(profileMapper::toDomain);
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
        // Buscar as entidades relacionadas para estabelecer as relações JPA
        var perfil = profileJpaRepository.findByCodPerfil(entity.getId().getCodPerfil())
                .orElseThrow(
                        () -> new IllegalArgumentException("Perfil não encontrado: " + entity.getId().getCodPerfil()));

        FunctionalityEntityJpaKey functionalityKey = new FunctionalityEntityJpaKey();
        functionalityKey.setCodSistema(entity.getId().getCodSistema());
        functionalityKey.setCodModulo(entity.getId().getCodModulo());
        functionalityKey.setCodRotina(entity.getId().getCodRotina());
        functionalityKey.setCodFuncionalidade(entity.getId().getCodFuncionalidade());

        var funcionalidade = functionalityJpaRepository.findById(functionalityKey)
                .orElseThrow(() -> new IllegalArgumentException("Funcionalidade não encontrada: " + functionalityKey));

        ProfileFunctionalityJpa jpaEntity = profileMapper.toEntity(entity);
        jpaEntity.setPerfil(perfil); // ✅ Setar a relação com Perfil
        jpaEntity.setFuncionalidade(funcionalidade); // ✅ Setar a relação com Funcionalidade
        ProfileFunctionalityJpa saved = profileFunctionalityJpaRepository.save(jpaEntity);
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

    // Atualizando a validade (dtFimValidade) da associação usuário-perfil
    @Override
    public int updateUserProfileValidity(Long idUsuario, String codPerfil, Long idUsuarioManutencao, boolean ativar,
            LocalDateTime novaValidade) {
        LocalDateTime now = LocalDateTime.now();
        int updated = 0;
        
        if (ativar) {
            // Inativa todos os outros registros ativos do usuário/perfil, exceto o que será ativado
            userProfileJpaRepository.inactivatePreviousActiveProfiles(
                idUsuario,
                codPerfil,
                null, // dtInicioValidade não é usado para inativar todos
                novaValidade // dtFimValidade do registro que será mantido ativo
            );
            // Ativa apenas o registro com dtFimValidade = novaValidade via UPDATE em lote
            updated = userProfileJpaRepository.activateUserProfile(
                idUsuario,
                codPerfil,
                novaValidade,
                idUsuarioManutencao,
                now
            );
        } else {
            // Inativa todos os registros do usuário/perfil via UPDATE em lote
            updated = userProfileJpaRepository.inactivateUserProfile(
                idUsuario,
                codPerfil,
                novaValidade,
                idUsuarioManutencao,
                now
            );
        }
        return updated;
    }

    @Override
    public void saveUserProfile(UserProfile userProfile) {
        UserProfileJpa entity = profileMapper.toEntity(userProfile);
        // Buscar e setar o perfil (obrigatório para JPA)
        ProfileEntityJpa perfil = profileJpaRepository.findByCodPerfil(userProfile.getId().getCodPerfil())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Perfil não encontrado: " + userProfile.getId().getCodPerfil()));
        entity.setPerfil(perfil);
        // Buscar e setar o usuário (obrigatório para JPA)
        var usuario = userRepositoryJpa.findById(userProfile.getId().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado: " + userProfile.getId().getIdUsuario()));
        entity.setUsuario(usuario);
        // Inativar registros antigos ativos (exceto o novo)
        userProfileJpaRepository.inactivatePreviousActiveProfiles(
                userProfile.getId().getIdUsuario(),
                userProfile.getId().getCodPerfil(),
                userProfile.getId().getDtInicioValidade(),
                userProfile.getId().getDtFimValidade());
                
        userProfileJpaRepository.save(entity);

    }
}
