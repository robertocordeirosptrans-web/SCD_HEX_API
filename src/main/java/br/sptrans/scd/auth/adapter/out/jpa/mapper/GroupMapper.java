package br.sptrans.scd.auth.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupProfileEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupProfileEntityJpaId;
import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupUserEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupUserEntityJpaId;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.GroupProfile;
import br.sptrans.scd.auth.domain.GroupProfileKey;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.GroupUserKey;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { UserMapper.class },
        imports = { GroupUserKey.class, GroupProfileKey.class })
public interface GroupMapper {

    // ── Group ────────────────────────────────────────────────────────────────

    @Mapping(target = "dtModi", source = "dtManutencao")
    @Mapping(target = "usuarioManutencao", source = "usuarioManutencao")
    @Mapping(target = "perfis", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    Group toDomain(GroupEntityJpa entity);

    @Mapping(target = "dtManutencao", source = "dtModi")
    @Mapping(target = "grupoPerfis", ignore = true)
    @Mapping(target = "grupoUsuarios", ignore = true)
    GroupEntityJpa toEntity(Group group);

    // ── GroupUser ─────────────────────────────────────────────────────────────

    @Mapping(target = "id", expression = "java(entity.getId() != null ? new GroupUserKey(entity.getId().getIdUsuario(), entity.getId().getCodGrupo()) : null)")
    @Mapping(target = "dtModi", source = "dtManutencao")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "usuarioManutencao", source = "usuarioManutencao")
    @Mapping(target = "grupo", ignore = true)
    GroupUser toDomain(GroupUserEntityJpa entity);

    default GroupUserEntityJpa toEntity(GroupUser groupUser) {
        if (groupUser == null) return null;
        GroupUserEntityJpaId id = new GroupUserEntityJpaId();
        id.setIdUsuario(groupUser.getId().getIdUsuario());
        id.setCodGrupo(groupUser.getId().getCodGrupo());
        GroupUserEntityJpa entity = new GroupUserEntityJpa();
        entity.setId(id);
        entity.setCodStatus(groupUser.getCodStatus());
        entity.setIdUsuarioManutencao(groupUser.getIdUsuarioManutencao());
        entity.setDtManutencao(groupUser.getDtModi());
        return entity;
    }

    // ── GroupProfile ──────────────────────────────────────────────────────────

    @Mapping(target = "id", expression = "java(entity.getId() != null ? new GroupProfileKey(entity.getId().getCodGrupo(), entity.getId().getCodPerfil()) : null)")
    @Mapping(target = "dtModi", expression = "java(entity.getDtManutencao() != null ? entity.getDtManutencao().toLocalDate() : null)")
    @Mapping(target = "usuarioManutencao", source = "usuarioManutencao")
    @Mapping(target = "grupo", ignore = true)
    @Mapping(target = "perfil", ignore = true)
    GroupProfile toDomain(GroupProfileEntityJpa entity);

    default GroupProfileEntityJpa toEntity(GroupProfile groupProfile) {
        if (groupProfile == null) return null;
        GroupProfileEntityJpaId id = new GroupProfileEntityJpaId();
        id.setCodGrupo(groupProfile.getId().getCodGrupo());
        id.setCodPerfil(groupProfile.getId().getCodPerfil());
        GroupProfileEntityJpa entity = new GroupProfileEntityJpa();
        entity.setId(id);
        entity.setCodStatus(groupProfile.getCodStatus());
        return entity;
    }
}
