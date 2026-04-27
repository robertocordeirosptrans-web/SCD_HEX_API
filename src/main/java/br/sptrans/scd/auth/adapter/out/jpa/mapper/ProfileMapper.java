package br.sptrans.scd.auth.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileFunctionalityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.ProfileFunctionalityJpaId;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserProfileJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserProfileJpaId;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.ProfileFunctionality;
import br.sptrans.scd.auth.domain.ProfileFunctionalityKey;
import br.sptrans.scd.auth.domain.UserProfile;
import br.sptrans.scd.auth.domain.UserProfileId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { UserMapper.class })
public interface ProfileMapper {

    // ── Profile ───────────────────────────────────────────────────────────────

    @Mapping(target = "dtModi", source = "dtManutencao")
    @Mapping(target = "usuarioManutencao", source = "usuarioManutencao")
    Profile toDomain(ProfileEntityJpa entity);

    @Mapping(target = "dtManutencao", source = "dtModi")
    @Mapping(target = "perfilFuncionalidades", ignore = true)
    ProfileEntityJpa toEntity(Profile profile);

    // ── ProfileFunctionality ─────────────────────────────────────────────────

    @Mapping(target = "id", source = "id")
    @Mapping(target = "dtInicioValidade", expression = "java(entity.getDtInicioValidade() != null ? entity.getDtInicioValidade().toLocalDate() : null)")
    @Mapping(target = "usuarioManutencao", source = "usuarioManutencao")
    @Mapping(target = "funcionalidade", ignore = true)
    @Mapping(target = "perfil", ignore = true)
    ProfileFunctionality toDomain(ProfileFunctionalityJpa entity);

    ProfileFunctionalityKey toKey(ProfileFunctionalityJpaId id);

    ProfileFunctionalityJpaId toJpaId(ProfileFunctionalityKey key);

    default ProfileFunctionalityJpa toEntity(ProfileFunctionality domain) {
        if (domain == null) return null;
        ProfileFunctionalityJpaId jpaId = toJpaId(domain.getId());
        ProfileFunctionalityJpa entity = new ProfileFunctionalityJpa();
        entity.setId(jpaId);
        entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao());
        if (domain.getDtInicioValidade() != null) {
            entity.setDtInicioValidade(domain.getDtInicioValidade().atStartOfDay());
        }
        // Nota: perfil e funcionalidade devem ser setados pelo adapter após buscar do banco
        // Este mapeador apenas mapeia os campos do domain para a entidade
        return entity;
    }

    // ── UserProfile ───────────────────────────────────────────────────────────

    @Mapping(target = "id", source = "id")
    @Mapping(target = "dtModi", source = "dtManutencao")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "perfil", ignore = true)
    UserProfile toDomain(UserProfileJpa entity);

    UserProfileId toUserProfileId(UserProfileJpaId id);
}
